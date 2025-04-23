
package com.example.homepage.ui

import android.content.ContentValues.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View.GONE;
import android.view.View.VISIBLE;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.enableEdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homepage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


class FriendsList : AppCompatActivity() {

    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_social)


        db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


        val friendName1 = findViewById<TextView>(R.id.user1)
        val friendName2 = findViewById<TextView>(R.id.user2)
        val friendName3 = findViewById<TextView>(R.id.user3)
        // supposed to display the names of friends, but I haven't set these yet.
        // need them to be the first + last name of queried users

        val requests = findViewById<Button>(R.id.requestbtn)
        //grab request list and display

        val friends = findViewById<Button>(R.id.friendsbtn)
        //grab friend list and display

        val addFriend1 = findViewById<Button>(R.id.add1)
        val addFriend2 = findViewById<Button>(R.id.add2)
        val addFriend3 = findViewById<Button>(R.id.add3)
        //update friend list
        val searchEditText = findViewById<EditText>(R.id.searchBar)
        //perform a query using friend code
        val search = findViewById<Button>(R.id.searchbtn)
        // activate search
        val declineFriend1 = findViewById<Button>(R.id.decline1)
        val declineFriend2 = findViewById<Button>(R.id.decline2)
        val declineFriend3 = findViewById<Button>(R.id.decline3)
        //remove from request list
        val socialPage = findViewById<Button>(R.id.socialPgBtn)
        val homeBtn = findViewById<Button>(R.id.homeButton)

        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        friends.setOnClickListener {
            if (declineFriend1.visibility == VISIBLE) declineFriend1.visibility = GONE
            if (declineFriend2.visibility == VISIBLE) declineFriend2.visibility = GONE
            if (declineFriend3.visibility == VISIBLE) declineFriend3.visibility = GONE
            if (addFriend1.visibility == VISIBLE) declineFriend1.visibility = GONE
            if (addFriend2.visibility == VISIBLE) declineFriend2.visibility = GONE
            if (addFriend3.visibility == VISIBLE) declineFriend3.visibility = GONE
            if (socialPage.visibility == GONE) socialPage.visibility = VISIBLE
            //display current friends ...   getFriends = query for friend list
            val friendRef = db.collection("users").document(userId).collection("friendsList")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
            //not sure how to display properly, only need first and last name to display.
        }


        val userFriendcodeRef = db.collection("users").document(userId)


        search.setOnClickListener{
            //display users with searched friend code, searchFriends is the query
            val searchFriends = userFriendcodeRef.whereEqualTo(friendCode, (searchEditText))
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting Friend List: ", exception)
                }

            //not sure how to display properly, only need first and last name to display.
        }


        val addFriend = hashMapOf(
            val email =
        val firstName = //get first name of target user
        val lastName =  //get last name of target user
        val friendCode = // get friend code of target user
        val userID = userId
        )



        //add friend buttons
        //need to add check if user is sending a friend request or accepting one. This would would call acceptRequest & addFriend and change the message displayed.
        //need to add current user to targets friends collection if friend request is sent.
        addFriend1.setOnClickListener{
            db.collection("users").document("userId")
                .collection("Friends").document(userId).set(addFriend)
                .addOnSuccessListener { Log.d(TAG, "Friend Successfully Added!")}
                .addOnFailureListener({ e -> Log.w(TAG, "Error adding Friend")})
        }
        addFriend2.setOnClickListener{
            db.collection("users").document(userId)
                .collection("Friends").document(userId).set(addFriend)
                .addOnSuccessListener { Log.d(TAG, "Friend Successfully Added!")}
                .addOnFailureListener({ e -> Log.w(TAG, "Error adding Friend")})
        }
        addFriend3.setOnClickListener{
            db.collection("users").document(userId)
                .collection("Friends").document(userId).set(addFriend)
                .addOnSuccessListener { Log.d(TAG, "Friend Successfully Added!")}
                .addOnFailureListener({ e -> Log.w(TAG, "Error adding Friend")})
        }

        // decline friend buttons
        declineFriend1.setOnClickListener{

            db.collection("users").document("userId").collection("Friends").document(userId)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Friend Successfully deleted")}
                .addOnFailureListener({ e -> Log.w(TAG, "Error deleting Friend")})
        }
        declineFriend2.setOnClickListener{

            db.collection("users").document("userId").collection("Friends").document(userId)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Friend Successfully deleted")}
                .addOnFailureListener({ e -> Log.w(TAG, "Error deleting Friend")})
        }
        declineFriend3.setOnClickListener{

            db.collection("users").document("userId").collection("Friends").document(userId)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Friend Successfully deleted")}
                .addOnFailureListener({ e -> Log.w(TAG, "Error deleting Friend")})
        }


    }
}