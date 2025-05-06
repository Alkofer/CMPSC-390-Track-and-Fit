package com.example.homepage.ui

import android.annotation.SuppressLint
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoginPage : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private val updateAchievementUrl = "https://updateuserachievement-wk7ebpefeq-uc.a.run.app"
    private val getAchievementsUrl = "https://getuserachievements-wk7ebpefeq-uc.a.run.app"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cardLogin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signUpButton = findViewById<Button>(R.id.SignBut)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SurveyPage::class.java)
            startActivity(intent)
        }


        val loginButton = findViewById<Button>(R.id.LogBut)
        loginButton.setOnClickListener {
            val emailEditText = findViewById<EditText>(R.id.et_email)
            val passwordEditText = findViewById<EditText>(R.id.et_password)
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                    updateDailyAchievement(userId)

                    Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun updateDailyAchievement(userId: String)
    {
        val userDoc = Firebase.firestore
            .collection("users")
            .document(userId)

        userDoc.get()
            .addOnSuccessListener { doc ->
                val lastLogin = doc.getString("lastLoginDate")
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

                if(lastLogin != today)
                {
                    userDoc.update("lastLoginDate", today,
                        "loginCount", FieldValue.increment(1)
                    ).addOnSuccessListener {
                        val ach = Achievements(this)
                        ach.updateUserAchievementProgress(
                            userId, "dedicated", updateAchievementUrl
                        )
                        ach.updateUserAchievementProgress(
                            userId, "dedicated2", updateAchievementUrl
                        )

                        doc.getString("dob")?.let {dobString ->
                            try {
                                val dobDate = SimpleDateFormat(
                                    "MM/dd/yyyy", Locale.US
                                ).parse(dobString)
                                val dobMd    = SimpleDateFormat(
                                    "MM/dd",      Locale.US
                                ).format(dobDate!!)
                                val todayMd  = SimpleDateFormat(
                                    "MM/dd",      Locale.US
                                ).format(Date())

                                if (dobMd == todayMd) {
                                    ach.updateUserAchievementProgress(
                                        userId, "birthday", updateAchievementUrl
                                    )
                                }
                            } catch (e: Exception){
                                Toast.makeText(this, "Failed to update bday", Toast.LENGTH_SHORT).show()
                            }
                        }


                        startActivity(Intent(this, MainActivity::class.java))
                    }
                        .addOnFailureListener { e ->
                            Toast.makeText(this,
                                "Error bumping login count: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Login fail", Toast.LENGTH_LONG
                ).show()
            }
    }
}