package com.example.homepage

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homepage.model.User

class Leaderboard : AppCompatActivity() {

    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter

    private val users = listOf(
        User("Alice", 120, 50, 30),
        User("Bob", 100, 70, 40),
        User("Charlie", 150, 60, 20),
        User("David", 90, 80, 50)
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView)
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)

        leaderboardAdapter = LeaderboardAdapter(users)
        leaderboardRecyclerView.adapter = leaderboardAdapter

        val mostActive = findViewById<Button>(R.id.MostActBtn)
        val mostWorkouts = findViewById<Button>(R.id.workoutsBtn)
        val longestStreak = findViewById<Button>(R.id.LongStreakBtn)

        mostActive.setOnClickListener {
            updateLeaderboard("mostActive")
        }
        mostWorkouts.setOnClickListener {
            updateLeaderboard("mostWorkouts")
        }
        longestStreak.setOnClickListener {
            updateLeaderboard("longestStreak")
        }

        // Default leaderboard
        updateLeaderboard("mostActive")
    }

    private fun updateLeaderboard(type: String) {
        val sortedUsers = when (type) {
            "mostActive" -> users.sortedByDescending { it.mostActive }
            "mostWorkouts" -> users.sortedByDescending { it.mostWorkouts }
            "longestStreak" -> users.sortedByDescending { it.longestStreak }
            else -> users
        }
        leaderboardAdapter.updateData(sortedUsers, type)
    }
}
