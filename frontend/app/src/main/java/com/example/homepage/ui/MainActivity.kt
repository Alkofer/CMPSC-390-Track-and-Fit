package com.example.homepage.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.example.homepage.Leaderboard
import com.example.homepage.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
            return
        }
        val userId = currentUser.uid

        findViewById<Button>(R.id.btn_push).setOnClickListener { launchWorkoutList("Push", userId) }
        findViewById<Button>(R.id.btn_pull).setOnClickListener { launchWorkoutList("Pull", userId) }
        findViewById<Button>(R.id.btn_legs).setOnClickListener { launchWorkoutList("Legs", userId) }
        val leaderboardButton = findViewById<ImageButton>(R.id.leaderboard)
        leaderboardButton.setOnClickListener {
            val intent = Intent(this, Leaderboard::class.java)
            startActivity(intent)
        }

        val achievementsButton = findViewById<ImageButton>(R.id.achievements)
        achievementsButton.setOnClickListener {
            val intent = Intent(this, AchievementsActivity::class.java)
            startActivity(intent)
        }


        val blacklistButton = findViewById<ImageButton>(R.id.blacklist)
        blacklistButton.setOnClickListener {
            val intent = Intent(this, RemoveBlacklist::class.java)
            startActivity(intent)
        }


        val profileButton: ImageButton = findViewById(R.id.btn_profile)
        profileButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }



    }

    private fun launchWorkoutList(workoutType: String, userId: String) {
        val intent = Intent(this, WorkoutList::class.java).apply {
            putExtra("WORKOUT_TYPE", workoutType)
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }
}