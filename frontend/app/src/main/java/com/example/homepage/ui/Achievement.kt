package com.example.homepage.ui

import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.homepage.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import android.widget.TextView
import android.view.View

class Achievement : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_achievement)

        val button = findViewById<Button>(R.id.mainButton)
        button?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

}
