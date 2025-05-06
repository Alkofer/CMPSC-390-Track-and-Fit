package com.example.homepage

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homepage.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.Query

class Leaderboard : AppCompatActivity() {

    @IgnoreExtraProperties
    data class User(
        val firstName: String = "",
        val lastName: String = "",
        val progressCount: Int = 0
    )

    @SuppressLint("MissingInflatedId", "DefaultLocale", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val tvYourBL = findViewById<TextView>(R.id.tvYourBlacklisted)
        val tvGlobalBL = findViewById<TextView>(R.id.tvGlobalBlacklisted)
        val tvYourSW = findViewById<TextView>(R.id.tvYourSecondWorkout)
        val tvGlobalSW = findViewById<TextView>(R.id.tvGlobalSecondWorkout)

        val tvBack = findViewById<TextView>(R.id.buttonBack)

        val myUserId = FirebaseAuth.getInstance().currentUser!!.uid

        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .get()
            .addOnSuccessListener { docs ->
                val total = docs.size()
                if (total == 0) {
                    tvYourBL.text= "0"
                    tvGlobalBL.text = "0.0"
                    return@addOnSuccessListener
                }

                var sumBL = 0
                var myBL = 0

                for (doc in docs) {
                    val blCount = (doc.get("blacklistedWorkouts") as? List<*>)?.size ?: 0
                    sumBL += blCount
                    if (doc.id == myUserId) {
                        myBL = blCount
                    }
                }

                val avgBL = sumBL.toDouble() / total
                tvYourBL.text = myBL.toString()
                tvGlobalBL.text = String.format("%.1f", avgBL)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show()
            }

        db.collection("users")
            .document(myUserId)
            .collection("achievements")
            .document("second_workout")
            .get()
            .addOnSuccessListener { doc ->
                val p = doc.getLong("progress")?.toInt() ?: 0
                tvYourSW.text = p.toString()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show()
            }

        db.collectionGroup("achievements")
            .get()
            .addOnSuccessListener { snaps ->
                val relevant = snaps.filter { it.id == "second_workout" }
                if (relevant.isEmpty()) {
                    tvGlobalSW.text = "0.0"
                    return@addOnSuccessListener
                }

                val sum = relevant.sumOf { it.getLong("progress")?.toInt() ?: 0 }

                val avg = sum.toDouble() / relevant.size
                tvGlobalSW.text = String.format("%.1f", avg)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show()
            }


        tvBack.setOnClickListener()
        {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}
