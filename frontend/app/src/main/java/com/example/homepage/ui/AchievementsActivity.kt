package com.example.homepage.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homepage.R
import com.example.homepage.adapter.AchievementAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request


data class AchievementModel(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var requiredProgress: Int = 0,
    var progress: Int = 0,
    var complete: Boolean = false,
    var finishCount: Int = 0
)

data class AchievementResponse(
    val achievements: List<AchievementModel>
)

class AchievementsActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AchievementAdapter
    private val achievementsList = mutableListOf<AchievementModel>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        recyclerView = findViewById(R.id.recyclerViewAchievements)
        adapter = AchievementAdapter(achievementsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadAchievements()

        db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userRef = db.collection("publicUsers").document(userId.toString())

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val finishCount = document.getString("finishCount") ?: "N/A"

                val achievementData = mapOf(
                    "finishCount" to finishCount
                )

                syncAchievementsLocally(achievementData, userId.toString())
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load user profile", Toast.LENGTH_SHORT).show()
        }

        val buttonBack: Button = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadAchievements() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            return
        }

        val url = "https://getuserachievements-wk7ebpefeq-uc.a.run.app?userId=$userId"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    Log.d("AchievementsActivity", "JSON Response: $jsonData")
                    val achievementResponse = Gson().fromJson(jsonData, AchievementResponse::class.java)
                    withContext(Dispatchers.Main) {
                        achievementsList.clear()
                        achievementResponse?.achievements?.let { achievementsList.addAll(it) }
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AchievementsActivity, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AchievementsActivity, "Network error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun syncAchievementsLocally(achievementData: Map<String, Any>, userId: String) {
        val publicUserRef = FirebaseFirestore.getInstance()
            .collection("publicUsers")
            .document(userId)

        publicUserRef.set(achievementData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Achievements synced", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to sync Achievements: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}