package com.example.homepage

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Leaderboard : AppCompatActivity() {





    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leaderboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val mostActive = findViewById<Button>(R.id.MostActBtn)
        val mostWorkouts = findViewById<Button>(R.id.workoutsBtn)
        val longestStreak = findViewById<Button>(R.id.LongStreakBtn)


        mostActive.setOnClickListener(){
            mostActiveView()
        }
        mostWorkouts.setOnClickListener(){
            mostWorkoutsView()
        }
        longestStreak.setOnClickListener(){
            longestStreakView()
        }
    }
    //function for buttons
    private fun mostActiveView(){

    }

    private fun mostWorkoutsView(){

    }

    private fun longestStreakView(){

    }

}