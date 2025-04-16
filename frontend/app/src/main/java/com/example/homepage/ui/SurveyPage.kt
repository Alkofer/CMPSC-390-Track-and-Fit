package com.example.homepage.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homepage.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class SurveyPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_survey_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<EditText>(R.id.email)
        val etFirstName = findViewById<EditText>(R.id.Firstname)
        val etLastName = findViewById<EditText>(R.id.Lastname)
        val etDOB = findViewById<EditText>(R.id.DOB)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnSubmit = findViewById<Button>(R.id.buttonSubmit)

        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val dob = etDOB.text.toString().trim()
            val password = etPassword.text.toString().trim()

            sendUserData(email, password, firstName, lastName, dob)
        }
    }

    private fun sendUserData(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        dob: String
    ) {
        val json = """
            {
                "email": "$email",
                "password": "$password",
                "firstName": "$firstName",
                "lastName": "$lastName",
                "dob": "$dob"
            }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toRequestBody(mediaType)

        val url = "https://createuser-wk7ebpefeq-uc.a.run.app"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@SurveyPage,
                        "Network error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful){
                        Log.d("Signup","User Created Successfully")
                        Toast.makeText(this@SurveyPage, "User created", Toast.LENGTH_LONG).show()
                        loginUser(email = email, password = password)
                        finish()
                    } else {
                        Log.e("Signup",responseBody.toString())
                        Toast.makeText(this@SurveyPage, "Error: $responseBody", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun addToFriendsList(){
        Log.d("Signup","Friends List Start")
        val etEmail = findViewById<EditText>(R.id.email)
        val etFirstName = findViewById<EditText>(R.id.Firstname)
        val etLastName = findViewById<EditText>(R.id.Lastname)

        val email = etEmail.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()

        val letters = ('A'..'Z').shuffled().take(3).joinToString("")
        val numbers = (100..999).random().toString()

        val friendCode = letters+numbers

        val id = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val json =  hashMapOf(
            "userId" to id,
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "friendCode" to friendCode
        )

        Log.d("Signup", "json: $json")

        val userDocRef = Firebase.firestore.collection("friendsList").document(id)
        userDocRef.set(json)
            .addOnSuccessListener {
                Log.d("Signup", "Success Saving")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("Signup", "Failed saving to collection $e")

                Toast.makeText(this, "Failed to update blacklist: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun loginUser(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show()
                    addToFriendsList()

                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

}
