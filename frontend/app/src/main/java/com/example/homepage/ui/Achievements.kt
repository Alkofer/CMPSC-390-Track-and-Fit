package com.example.homepage.ui

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

class Achievements(private val activity: Activity) {
    fun fetchUserAchievements(userId: String,getAchievementsUrl: String) {
        val url = "$getAchievementsUrl?userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()
                if (response.isSuccessful) {
                    Log.d("WorkoutList", "Achievements JSON: $jsonData")
                } else {
                    activity.runOnUiThread {
                        Toast.makeText(activity, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    fun updateUserAchievementProgress(userId: String, achievementId: String, updateAchievementUrl: String) {
        val achievementDocRef = Firebase.firestore
            .collection("users")
            .document(userId)
            .collection("achievements")
            .document(achievementId)

        achievementDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentProgress = (document.getLong("progress") ?: 0L).toInt()
                    val finishCountField = document.getLong("finishCount") ?: 0L
                    val target = if (finishCountField == 0L) {
                        (document.getLong("requiredProgress") ?: 0L).toInt()
                    } else {
                        finishCountField.toInt()
                    }
                    val complete = document.getBoolean("complete") ?: false

                    if (currentProgress + 1 <= target) {
                        val newProgress = currentProgress + 1
                        val jsonObject = JSONObject().apply {
                            put("userId", userId)
                            put("achievementId", achievementId)
                            put("progress", newProgress)
                            put("finishCount", target)
                            put("complete", complete)
                        }
                        sendAchievementUpdate(jsonObject, updateAchievementUrl)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Error getting achievement ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun sendAchievementUpdate(jsonObject: JSONObject, updateAchievementUrl: String) {
        val jsonString = jsonObject.toString()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonString.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(updateAchievementUrl)
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity.runOnUiThread {
                    Toast.makeText(activity, "Achievement update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                activity.runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(activity, "Achievement updated successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}