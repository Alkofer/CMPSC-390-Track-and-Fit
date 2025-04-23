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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class SurveyPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_survey_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_signup)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<EditText>(R.id.et_signup_email)
        val etFirstName = findViewById<EditText>(R.id.et_signup_firstname)
        val etLastName = findViewById<EditText>(R.id.et_signup_lastname)
        val etDOB = findViewById<EditText>(R.id.et_signup_dob)
        val etPassword = findViewById<EditText>(R.id.et_signup_password)
        val btnSubmit = findViewById<Button>(R.id.btn_signup_submit)

        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val dob = etDOB.text.toString().trim()
            val password = etPassword.text.toString().trim()

            val userCode = generateRandomCode()
            sendUserData(email, password, firstName, lastName, dob, userCode)
        }
    }

    private fun generateRandomCode(): String {
        val letters = ('A'..'Z') + ('a'..'z')
        val digits = ('0'..'9')
        val randomLetters = List(3) { letters.random() }
        val randomDigits = List(3) { digits.random() }
        return (randomLetters + randomDigits).shuffled().joinToString("")
    }

    private fun sendUserData(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        dob: String,
        userCode: String
    ) {
        val json = """
            {
                "email": "$email",
                "password": "$password",
                "firstName": "$firstName",
                "lastName": "$lastName",
                "dob": "$dob",
                "userCode": "$userCode"
            }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toRequestBody(mediaType)
        val url = "https://createuser-wk7ebpefeq-uc.a.run.app"

        OkHttpClient().newCall(Request.Builder().url(url).post(requestBody).build())
            .enqueue(object : Callback {
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
                        if (response.isSuccessful) {
                            Toast.makeText(this@SurveyPage, "User created", Toast.LENGTH_LONG).show()

                            // Sign in the user before writing to Firestore
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener {
                                    saveUserToFirestore(firstName, lastName, dob, userCode)
                                    val intent = Intent(this@SurveyPage, LoginPage::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this@SurveyPage,
                                        "Sign in failed: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                        } else {
                            Toast.makeText(this@SurveyPage, "Error: $responseBody", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
    }

    private fun saveUserToFirestore(
        firstName: String,
        lastName: String,
        dob: String,
        userCode: String
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        if (uid != null) {
            val userMap = mapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "dob" to dob,
                "userCode" to userCode,
                "createdAt" to Timestamp.now()
            )

            db.collection("users").document(uid)
                .set(userMap)
                .addOnSuccessListener {
                    Log.d("Firestore", "User document created successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Failed to create user document", e)
                }
        } else {
            Log.e("Firestore", "UID is null. Cannot create Firestore user document.")
        }
    }
}
