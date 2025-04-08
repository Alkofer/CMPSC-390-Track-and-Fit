package com.example.homepage.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle

import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.homepage.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    //private val auth = FirebaseAuth.getInstance()
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val profileName = findViewById<TextView>(R.id.titleName)
        val profileFName = findViewById<TextView>(R.id.profileFirstName)
        val profileLName = findViewById<TextView>(R.id.profileLastName)
        val profileEmail = findViewById<TextView>(R.id.profileEmail)
        val profileUserId = findViewById<TextView>(R.id.titleUsername)
        //val profileUsername = findViewById<TextView>(R.id.profileUsername)
        val profileBio = findViewById<TextView>(R.id.bioEditText)
        val editButton = findViewById<TextView>(R.id.editButton)
        val profileDOB = findViewById<TextView>(R.id.profileDOB)

        // Initialize Firestore + Auth
        db = FirebaseFirestore.getInstance()

        val userId = intent.getStringExtra("userId")
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            profileEmail.text = currentUser.email
        }

        if (userId == null) {
            Toast.makeText(this, "Missing user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val firstName = document.getString("firstName") ?: "N/A"
                val lastName = document.getString("lastName") ?: "N/A"
                val bio = document.getString("bio") ?: "No bio added yet."
                val dob = document.getString("dob") ?: "N/A"


                profileName.text = "$firstName $lastName"
                profileFName.text = firstName
                profileLName.text = lastName
                //profileUsername.text = userId
                profileUserId.text = userId
                profileBio.text = bio
                profileDOB.text = dob

            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load user profile", Toast.LENGTH_SHORT).show()
            }

        editButton.setOnClickListener {
            val newBio = profileBio.text.toString()

            userRef.update("bio", newBio).addOnSuccessListener {
                Toast.makeText(this, "Bio updated", Toast.LENGTH_SHORT).show()


                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update bio", Toast.LENGTH_SHORT).show()
            }
        }

    }
}