package com.example.homepage.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homepage.R
import com.example.homepage.adapter.WorkoutAdapter
import com.example.homepage.model.Workout
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
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


class WorkoutList : AppCompatActivity(), WorkoutAdapter.OnWorkoutActionListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter

    private val dislikedWorkouts = mutableSetOf<String>()

    private lateinit var workoutType: String
    private lateinit var userId: String

    private val getRoutineUrl = "https://getworkoutroutine-wk7ebpefeq-uc.a.run.app"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workout_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        workoutType = intent.getStringExtra("WORKOUT_TYPE") ?: ""
        userId = intent.getStringExtra("USER_ID") ?: ""
        if (userId.isEmpty()) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        recyclerView = findViewById(R.id.workoutRecyclerView)
        workoutAdapter = WorkoutAdapter(emptyList(), this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = workoutAdapter

        findViewById<Button>(R.id.btnFinish).setOnClickListener { finishRoutine() }

        fetchWorkouts(workoutType)

    }
    private fun fetchWorkouts(workoutType: String) {
        dislikedWorkouts.clear()

        val json = """
        {
            "userId": "$userId",
            "workoutType": "$workoutType"
        }
    """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(getRoutineUrl)
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@WorkoutList, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                android.util.Log.d("WorkoutList", "Response body: $body")

                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@WorkoutList, "Error: $body", Toast.LENGTH_LONG).show()
                    }
                    return
                }

                try {
                    val jsonObject = JSONObject(body ?: "")
                    if (!jsonObject.has("workouts")) {
                        runOnUiThread {
                            Toast.makeText(this@WorkoutList, "No workouts found in response.", Toast.LENGTH_LONG).show()
                        }
                        return
                    }
                    val workoutsArray = jsonObject.getJSONArray("workouts")
                    val workoutList = mutableListOf<Workout>()
                    for (i in 0 until workoutsArray.length()) {
                        val item = workoutsArray.getJSONObject(i)
                        val id = item.getString("id")
                        val name = item.optString("name", "Unnamed Workout")
                        val muscleGroup = item.optString("muscleGroup", "")
                        workoutList.add(Workout(id, name, muscleGroup))
                    }
                    runOnUiThread {
                        workoutAdapter.setData(workoutList)
                    }
                } catch (ex: Exception) {
                    android.util.Log.e("WorkoutList", "JSON Parsing error", ex)
                    runOnUiThread {
                        Toast.makeText(this@WorkoutList, "Parsing error: ${ex.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    override fun onLikeClicked(workout: Workout) {
        Toast.makeText(this, "Liked: ${workout.name}", Toast.LENGTH_SHORT).show()

    }

    override fun onDislikeClicked(workout: Workout) {
        dislikedWorkouts.add(workout.id)
        Toast.makeText(this, "Disliked: ${workout.name}", Toast.LENGTH_SHORT).show()
    }

    private fun finishRoutine() {
        if (dislikedWorkouts.isEmpty()) {
            Toast.makeText(this, "Workout finished! No workouts were disliked.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userDocRef = Firebase.firestore.collection("users").document(userId)
        userDocRef.update("blacklistedWorkouts", FieldValue.arrayUnion(*dislikedWorkouts.toTypedArray()))
            .addOnSuccessListener {
                Toast.makeText(this, "Workout finished! Blacklist updated.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update blacklist: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}