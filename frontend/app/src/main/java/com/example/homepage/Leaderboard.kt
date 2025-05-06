package com.example.homepage

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.Query

class Leaderboard : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter

    @IgnoreExtraProperties
    data class User(
        val firstName: String = "",
        val lastName: String = "",
        val progressCount: Int = 0
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        Log.d(TAG, "onCreate started")
        Toast.makeText(this, "Leaderboard started", Toast.LENGTH_SHORT).show()

        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView)
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)


        val userList = mutableListOf<User>()
        leaderboardAdapter = LeaderboardAdapter(userList)
        leaderboardRecyclerView.adapter = leaderboardAdapter


        db = FirebaseFirestore.getInstance()
        val userRef = db.collection("publicUsers").orderBy("progressCount", Query.Direction.DESCENDING)

        Log.d(TAG, "Preparing Firestore query")
        userRef.get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Firestore success: ${documents.size()} documents found")
                for (document in documents) {
                    val firstName = document.getString("firstName") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    val progressCount = document.getLong("progressCount")?.toInt() ?: 0

                    val user = User(firstName, lastName, progressCount)
                    userList.add(user)
                    Log.d(TAG, "Parsed user: $user")
                }

                leaderboardAdapter.notifyDataSetChanged()
                Log.d(TAG, "Final userList size: ${userList.size}")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents", exception)
            }



    }

}
