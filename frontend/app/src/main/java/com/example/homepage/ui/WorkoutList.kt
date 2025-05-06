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
import com.example.homepage.ui.Achievements


class WorkoutList : AppCompatActivity(), WorkoutAdapter.OnWorkoutActionListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter
    private val dislikedWorkouts = mutableSetOf<String>()

    private lateinit var workoutType: String
    private lateinit var userId: String

    private val getRoutineUrl = "https://getworkoutroutine-wk7ebpefeq-uc.a.run.app"
    private val updateAchievementUrl = "https://updateuserachievement-wk7ebpefeq-uc.a.run.app"
    private val getAchievementsUrl = "https://getuserachievements-wk7ebpefeq-uc.a.run.app"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workout_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        workoutType = intent.getStringExtra("WORKOUT_TYPE") ?: ""
        userId  = intent.getStringExtra("USER_ID") ?: ""
        if (userId.isEmpty()) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_LONG).show()
            finish(); return
        }

        recyclerView = findViewById(R.id.workoutRecyclerView)
        workoutAdapter = WorkoutAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter  = workoutAdapter

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

        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(getRoutineUrl)
            .post(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@WorkoutList, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val text = response.body?.string()
                Log.d("WorkoutList", text ?: "no-body")

                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@WorkoutList, "Error: $text", Toast.LENGTH_LONG).show()
                    }
                    return
                }

                try {
                    val arr = JSONObject(text ?: "{}").getJSONArray("workouts")
                    val list = mutableListOf<Workout>()
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        list += Workout(
                            id = obj.getString("id"),
                            name = obj.optString("name", "Unnamed Workout"),
                            muscleGroup = obj.optString("muscleGroup", "")
                        )
                    }
                    runOnUiThread {
                        workoutAdapter.setData(list)
                    }
                } catch (ex: Exception) {
                    Log.e("WorkoutList", "JSON error", ex)
                }
            }
        })
    }

    override fun onLikeClicked(workout: Workout) {
    }

    override fun onDislikeClicked(workout: Workout) {
        dislikedWorkouts.add(workout.id)
    }

    private fun finishRoutine() {

        val ach = Achievements(this)
        ach.fetchUserAchievements(userId,getAchievementsUrl)
        ach.updateUserAchievementProgress(userId, "first_workout", updateAchievementUrl)
        ach.updateUserAchievementProgress(userId, "first_steps", updateAchievementUrl)
        ach.updateUserAchievementProgress(userId, "second_workout", updateAchievementUrl)

        if (dislikedWorkouts.isEmpty()) {
            finish()
            return
        }

        val userDocRef = Firebase.firestore.collection("users").document(userId)
        userDocRef.update("blacklistedWorkouts", FieldValue.arrayUnion(*dislikedWorkouts.toTypedArray()))
            .addOnSuccessListener {
                finish()
            }
    }


}
