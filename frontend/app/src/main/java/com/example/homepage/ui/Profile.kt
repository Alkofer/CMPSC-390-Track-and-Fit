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
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

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
        val profileUserId = findViewById<TextView>(R.id.friendCode)
        //val profileUsername = findViewById<TextView>(R.id.profileUsername)
        val profileBio = findViewById<TextView>(R.id.bioEditText)
        val editButton = findViewById<TextView>(R.id.homeButton)
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
                val userCode = document.getString("userCode") ?: "N/A"


                profileName.text = "$firstName $lastName"
                profileFName.text = firstName
                profileLName.text = lastName
                //profileUsername.text = userId
                profileUserId.text = userCode
                profileBio.text = bio
                profileDOB.text = dob

                val profileData = mapOf(
                    "userCode" to userCode,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "bio" to profileBio
                )

                callSyncPublicUserProfile(profileData) { success, message ->
                    if (success) {
                        //Toast.makeText(this, "Synced Profile", Toast.LENGTH_SHORT).show()
                    } else {
                        //Toast.makeText(this, "Failed to sync", Toast.LENGTH_SHORT).show()
                    }
                }

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


    private fun callSyncPublicUserProfile(profileData: Map<String, Any>, onResult: (Boolean, String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            onResult(false, "User not authenticated")
            return
        }

        user.getIdToken(true).addOnSuccessListener { result ->
            val idToken = result.token

            val url = "https://us-central1-track-and-fit-449302.cloudfunctions.net/syncPublicUserProfile"

            val json = JSONObject()
            json.put("profile", JSONObject(profileData))

            val requestBody = json.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $idToken")
                .addHeader("Content-Type", "application/json")
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onResult(false, "Network error: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    if (response.isSuccessful) {
                        onResult(true, body)
                    } else {
                        onResult(false, "Error: ${response.code} - $body")
                    }
                }
            })
        }.addOnFailureListener { e ->
            onResult(false, "Failed to get ID token: ${e.message}")
        }
    }


}