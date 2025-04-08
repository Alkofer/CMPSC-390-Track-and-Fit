package com.example.homepage.ui

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.homepage.R

class Social : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_social)


        val Requests = findViewById<Button>(R.id.requestbtn)
        //grab request list and display

        val Friends = findViewById<Button>(R.id.friendsbtn)
        //grab friend list and display

        val addFriend1 = findViewById<Button>(R.id.add1)
        //update friend list
        val addFriend2 = findViewById<Button>(R.id.add2)
        //update friend list
        val addFriend3 = findViewById<Button>(R.id.add3)
        //update friend list
        val searchEditText = findViewById<EditText>(R.id.searchBar)
        //search in whatever list is currently on.
        val search = findViewById<Button>(R.id.searchbtn)
        // activate search
        val declineFriend1 = findViewById<Button>(R.id.decline1)
        //remove from request list
        val declineFriend2 = findViewById<Button>(R.id.decline2)
        //remove from request list
        val declineFriend3 = findViewById<Button>(R.id.decline3)
        //remove from request list
        val socialPage = findViewById<Button>(R.id.socialPgBtn)

        val homeBtn = findViewById<Button>(R.id.homeButton)

        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        Requests.setOnClickListener {
            if (declineFriend1.visibility == GONE) declineFriend1.visibility = VISIBLE
            if (declineFriend2.visibility == GONE) declineFriend2.visibility = VISIBLE
            if (declineFriend3.visibility == GONE) declineFriend3.visibility = VISIBLE
            if (addFriend1.visibility == GONE) addFriend1.visibility = VISIBLE
            if (addFriend2.visibility == GONE) addFriend2.visibility = VISIBLE
            if (addFriend3.visibility == GONE) addFriend3.visibility = VISIBLE
            if (socialPage.visibility == GONE) socialPage.visibility = VISIBLE
            //display incoming requests ...  exports.getFriendRequests

        }

        Friends.setOnClickListener {
            if (declineFriend1.visibility == VISIBLE) declineFriend1.visibility = GONE
            if (declineFriend2.visibility == VISIBLE) declineFriend2.visibility = GONE
            if (declineFriend3.visibility == VISIBLE) declineFriend3.visibility = GONE
            if (addFriend1.visibility == VISIBLE) declineFriend1.visibility = GONE
            if (addFriend2.visibility == VISIBLE) declineFriend2.visibility = GONE
            if (addFriend3.visibility == VISIBLE) declineFriend3.visibility = GONE
            if (socialPage.visibility == GONE) socialPage.visibility = VISIBLE
            //display current friends ...   exports.getFriendsList

        }

        search.setOnClickListener{

        }
        addFriend1.setOnClickListener{

        }
        addFriend2.setOnClickListener{

        }
        addFriend3.setOnClickListener{

        }
        declineFriend1.setOnClickListener{

        }
        declineFriend2.setOnClickListener{

        }
        declineFriend3.setOnClickListener{

        }
    }
}