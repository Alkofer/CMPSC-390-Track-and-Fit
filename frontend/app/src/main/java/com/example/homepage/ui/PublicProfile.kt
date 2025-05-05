package com.example.homepage.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.homepage.R
import com.google.firebase.firestore.FirebaseFirestore

class PublicProfile : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_profile)

        val profileName = findViewById<TextView>(R.id.titleName)
        val profileFName = findViewById<TextView>(R.id.profileFirstName)
        val profileLName = findViewById<TextView>(R.id.profileLastName)
        //val profileEmail = findViewById<TextView>(R.id.profileEmail)
        val profileUserId = findViewById<TextView>(R.id.friendCode)
        val profileBio = findViewById<TextView>(R.id.bioEditText)
        //val profileDOB = findViewById<TextView>(R.id.profileDOB)

        db = FirebaseFirestore.getInstance()

        val intent = intent
        val userCode = intent.getStringExtra("UserCode")

        if (userCode == null) {
            Toast.makeText(this, "Missing user code", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Query publicUsers using userCode as a field
        db.collection("publicUsers")
            .whereEqualTo("userCode", userCode)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val firstName = document.getString("firstName") ?: "N/A"
                    val lastName = document.getString("lastName") ?: "N/A"
                    val bio = document.getString("bio") ?: "No bio added yet."
                    val foundCode = document.getString("userCode") ?: "N/A"

                    profileName.text = "$firstName $lastName"
                    profileFName.text = firstName
                    profileLName.text = lastName
                    profileUserId.text = foundCode
                    profileBio.text = bio

                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load user profile", Toast.LENGTH_SHORT).show()
            }

        val homeBtn = findViewById<Button>(R.id.homeButton)
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
