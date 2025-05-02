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

    //private val auth = FirebaseAuth.getInstance()
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val profileName = findViewById<TextView>(R.id.titleName)
        val profileFName = findViewById<TextView>(R.id.profileFirstName)
        val profileLName = findViewById<TextView>(R.id.profileLastName)
        val profileEmail = findViewById<TextView>(R.id.profileEmail)
        val profileUserId = findViewById<TextView>(R.id.friendCode)
        //val profileUsername = findViewById<TextView>(R.id.profileUsername)
        val profileBio = findViewById<TextView>(R.id.bioEditText)
        val profileDOB = findViewById<TextView>(R.id.profileDOB)

        // Initialize Firestore + Auth
        db = FirebaseFirestore.getInstance()

        val intent = intent
        val UserCode = intent.getStringExtra("UserCode")
        //val targetUser = FirebaseAuth.getInstance().currentUser

//        if (targetUser != null) {
//            profileEmail.text = targetUser.email
//        }

        if (UserCode == null) {
            Toast.makeText(this, "Target Missing user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userRef = db.collection("publicProfile").document(UserCode)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val firstName = document.getString("firstName") ?: "N/A"
                val lastName = document.getString("lastName") ?: "N/A"
                val bio = document.getString("bio") ?: "No bio added yet."
                val userCode = document.getString("userCode") ?: "N/A"


                profileName.text = "$firstName $lastName"
                profileFName.text = firstName
                profileLName.text = lastName
                profileUserId.text = userCode
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