package com.example.homepage.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.homepage.R
import com.google.firebase.firestore.FirebaseFirestore

class FriendsList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.friends_list)
        findViewById<Button>(R.id.searchBtn).setOnClickListener {
            val friendCode = findViewById<EditText>(R.id.friendCodeBox).text.toString()
            FirebaseFirestore.getInstance().collection("friendsList")
                .whereEqualTo("friendCode", friendCode)
                .get()
                .addOnSuccessListener { documents ->
                    Log.d("FriendsList", "Success Grabbing Docs")

                    val doc = documents.documents[0]
                    val name = doc.getString("name")
                    val email = doc.getString("email")
                    val code = doc.getString("friendCode")

                    findViewById<TextView>(R.id.results).setText("Found User Name: $name Email: $email Friend Code: $code")
                }
                .addOnFailureListener{ e ->
                    Log.e("FriendsList", "Failed $e")
                }
        }
    }
}