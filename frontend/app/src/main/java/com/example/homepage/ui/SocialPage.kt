
package com.example.homepage.ui

import android.content.ContentValues.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.enableEdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homepage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


class SocialPage : AppCompatActivity() {

    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_social)


        db = FirebaseFirestore.getInstance()
        val userRef = db.collection("publicUsers")

        val username1 = findViewById<TextView>(R.id.user1)
        val viewProfile1 = findViewById<Button>(R.id.view1)


        val searchEditText = findViewById<EditText>(R.id.searchBar)
        val search = findViewById<Button>(R.id.searchbtn)

        val homeBtn = findViewById<Button>(R.id.homeButton)
        val cardResult = findViewById<androidx.cardview.widget.CardView>(R.id.card_result)
        cardResult.visibility = View.GONE
        username1.visibility = View.GONE
        viewProfile1.visibility = View.GONE


        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }



        search.setOnClickListener {
            Log.d(TAG, "Search button clicked")

            val searchFriends = searchEditText.text.toString()

            userRef.whereEqualTo("userCode", searchFriends)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->

                    cardResult.visibility = View.VISIBLE
                    if (documents.isEmpty) {
                        Log.d(TAG, "No documents found for code: $searchFriends")
                        username1.text = "User not found"
                        username1.visibility = VISIBLE
                        viewProfile1.visibility = View.GONE
                        return@addOnSuccessListener
                    }

                    val document = documents.documents[0]
                    Log.d(TAG, "Document data: ${document.data}")

                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val fullName = listOfNotNull(firstName, lastName).joinToString(" ")

                    username1.text = fullName
                    username1.visibility = View.VISIBLE
                    viewProfile1.visibility = View.VISIBLE
                    Log.d(TAG, "Full Name: $fullName")
                }

                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching user: ${exception.message}", exception)
                    username1.text = "Error loading user: ${exception.message}"
                    viewProfile1.visibility = View.GONE
                    username1.visibility = View.VISIBLE
                    cardResult.visibility = View.VISIBLE
                }

            }

        viewProfile1.setOnClickListener{
            //pull friend code from searched friend
            val searchFriendCode = searchEditText.text.toString()

            val intent = Intent(applicationContext, PublicProfile::class.java)

            intent.putExtra("UserCode", searchFriendCode)

            startActivity(intent)

        }



    }
}