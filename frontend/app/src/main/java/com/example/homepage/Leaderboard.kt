package com.example.homepage

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Leaderboard : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter


    data class User(
        val firstName: String = "",
        val lastName: String = "",
        val progressCount: Int = 0
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)


        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView)
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)


        val userList = mutableListOf<User>()
        leaderboardAdapter = LeaderboardAdapter(userList)
        leaderboardRecyclerView.adapter = leaderboardAdapter


        db = FirebaseFirestore.getInstance()
        val userRef = db.collection("publicUsers").orderBy("progressCount", Query.Direction.DESCENDING)


        userRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    userList.add(user)
                }

                leaderboardAdapter.notifyDataSetChanged()
                Log.d(TAG, "Retrieved ${userList.size} users")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }



    }

}
