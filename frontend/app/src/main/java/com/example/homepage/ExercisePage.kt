package com.example.homepage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.homepage.R.id.Sets_Text
import com.example.homepage.R.id.mainButton
import com.example.homepage.model.Workout
import com.example.homepage.ui.MainActivity
import org.json.JSONObject

class ExercisePage : AppCompatActivity() {
    private lateinit var workoutType: String
    private lateinit var userId: String

    private val dislikedWorkouts = mutableSetOf<String>()

    private lateinit var workoutDesc: String    //To change the description of the exercise
    private lateinit var workoutName: String    //To change the displayed workout name
    var setsDone = 0                    //To keep track of sets completed
    var setsText: String = "Sets done: "       //To use when displaying sets completed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exercise_page)

        val button = findViewById<Button>(R.id.mainButton); //button to take the user back to the main page
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val Sets_Text = findViewById<TextView>(R.id.Sets_Text)

        val buttonPlus = findViewById<Button>(R.id.SetPlus_Button); //button to increase the amount of sets done for tracking purposes
        button.setOnClickListener {
            val setsDone = setsDone + 1
            Sets_Text.setText(setsText + setsDone)
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