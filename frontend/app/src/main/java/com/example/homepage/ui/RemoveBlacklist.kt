package com.example.homepage.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.homepage.R
import com.example.homepage.adapter.BlacklistAdapter
import com.example.homepage.model.Workout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RemoveBlacklist : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BlacklistAdapter
    private val workoutList = mutableListOf<Workout>()

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_blacklist)

        val btnReturnHome = findViewById<Button>(R.id.btnReturnHome)
        btnReturnHome.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.blacklistRecyclerView)
        adapter = BlacklistAdapter(workoutList) { workout ->
            removeFromBlacklist(workout)
        }
        recyclerView.adapter = adapter

        if (userId.isEmpty()) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchBlacklistedWorkouts()
    }

    private fun fetchBlacklistedWorkouts() {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                val ids = doc.get("blacklistedWorkouts") as? List<String> ?: emptyList()
                if (ids.isEmpty()) {
                    Toast.makeText(this, "No blacklisted workouts.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                workoutList.clear()
                for (id in ids) {
                    db.collection("workouts").document(id).get()
                        .addOnSuccessListener { workoutDoc ->
                            val name = workoutDoc.getString("name") ?: "Unnamed"
                            val group = workoutDoc.getString("muscleGroup") ?: ""
                            workoutList.add(Workout(id, name, group))
                            adapter.notifyDataSetChanged()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load blacklist.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeFromBlacklist(workout: Workout) {
        db.collection("users").document(userId)
            .update("blacklistedWorkouts", FieldValue.arrayRemove(workout.id))
            .addOnSuccessListener {
                workoutList.remove(workout)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Removed: ${workout.name}", Toast.LENGTH_SHORT).show()

                db.collection("workouts").document(workout.id)
                    .update("blacklisted", false)

            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to remove ${workout.name}", Toast.LENGTH_SHORT).show()
            }
    }
}
