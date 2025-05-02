
package com.example.homepage.ui

import android.content.ContentValues.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        //val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val userRef = db.collection("publicUsers")

        val username1 = findViewById<TextView>(R.id.user1)
        val username2 = findViewById<TextView>(R.id.user2)
        val username3 = findViewById<TextView>(R.id.user3)



        val viewProfile1 = findViewById<TextView>(R.id.view1)
        val viewProfile2 = findViewById<TextView>(R.id.view2)
        val viewProfile3 = findViewById<TextView>(R.id.view3)

        //update friend list
        val searchEditText = findViewById<EditText>(R.id.searchBar)
        //perform a query using friend code
        val search = findViewById<Button>(R.id.searchbtn)
        // activate search

        val homeBtn = findViewById<Button>(R.id.homeButton)

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
                    if (documents.isEmpty) {
                        Log.d(TAG, "No documents found for code: $searchFriends")
                        username1.text = "User not found"
                        return@addOnSuccessListener
                    }

                    val document = documents.documents[0]
                    Log.d(TAG, "Document data: ${document.data}")

                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val fullName = listOfNotNull(firstName, lastName).joinToString(" ")

                    username1.text = fullName
                    Log.d(TAG, "Full Name: $fullName")

                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching user: ${exception.message}", exception)
                    username1.text = "Error loading user: ${exception.message}"
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