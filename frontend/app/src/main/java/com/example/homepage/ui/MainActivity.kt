package com.example.homepage.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
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

        val button = findViewById<Button>(R.id.btn_login);
        button.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_push).setOnClickListener { launchWorkoutList("Push", userId) }
        findViewById<Button>(R.id.btn_pull).setOnClickListener { launchWorkoutList("Pull", userId) }
        findViewById<Button>(R.id.btn_legs).setOnClickListener { launchWorkoutList("Legs", userId) }
    }

    private fun launchWorkoutList(workoutType: String, userId: String) {
        val intent = Intent(this, WorkoutList::class.java).apply {
            putExtra("WORKOUT_TYPE", workoutType)
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }
}