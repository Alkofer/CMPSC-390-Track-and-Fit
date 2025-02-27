package com.example.homepage.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homepage.R
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
                    if (response.isSuccessful) {
                        Toast.makeText(this@SurveyPage, "User created", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@SurveyPage, LoginPage::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@SurveyPage, "Error: $responseBody", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
