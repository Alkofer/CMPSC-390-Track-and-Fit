package com.example.homepage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.homepage.R.id.mainButton
import com.example.homepage.ui.MainActivity

class ExercisePage : AppCompatActivity() {
    private lateinit var workoutType: String
    private lateinit var userId: String

    private val dislikedWorkouts = mutableSetOf<String>()

    private lateinit var workoutDesc: String    //To change the description of the exercise
    private lateinit var workoutName: String    //To change the displayed workout name
    private var setsDone = 0                    //To keep track of sets completed
    private lateinit var setsText: String       //To use when displaying sets completed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exercise_page)

        val button = findViewById<Button>(R.id.mainButton);
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        fun fetchWorkouts(workoutType: String) { //removes blacklisted workouts
            dislikedWorkouts.clear()

            val json = """
        {
            "userId": "$userId",
            "workoutType": "$workoutType"
        }
    """.trimIndent()





            
        }
    }
}