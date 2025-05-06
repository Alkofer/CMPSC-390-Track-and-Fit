package com.example.homepage.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homepage.R
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp.getInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

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

            sendUserData(email, password, firstName, lastName, normalize(dob))
        }
    }


    private fun normalize(input: String): String
    {
        val digits = input.filter(Char::isDigit)

        return try {
            val parser = SimpleDateFormat("MMddyyyy", Locale.US)
            val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            val date  = parser.parse(digits)
            formatter.format(date!!)
        } catch (e: ParseException) {
            input
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
                    if (response.isSuccessful) {

                        FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener { authResult ->
                                val uid = authResult.user!!.uid

                                val userRef = Firebase.firestore
                                    .collection("users")
                                    .document(uid)

                                userRef.set(
                                    mapOf(
                                        "loginCount" to 0L,
                                        "lastLoginDate" to ""
                                    ),
                                    SetOptions.merge()
                                )
                                    .addOnCompleteListener{
                                        Toast.makeText(this@SurveyPage, "User created", Toast.LENGTH_LONG).show()
                                        val intent = Intent(this@SurveyPage, LoginPage::class.java)
                                        startActivity(intent)
                                        finish()
                                    }


                            }
                    } else {
                        Toast.makeText(this@SurveyPage, "Error: $responseBody", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
