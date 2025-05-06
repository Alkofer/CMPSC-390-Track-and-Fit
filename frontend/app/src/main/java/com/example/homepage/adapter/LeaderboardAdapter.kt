package com.example.homepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homepage.model.User

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val progressCount: Int = 0
)

class LeaderboardAdapter(private var users: MutableList<Leaderboard.User>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.userName)
        val statTextView: TextView = view.findViewById(R.id.userStat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.nameTextView.text = "${user.firstName} ${user.lastName}"
        holder.statTextView.text = user.progressCount.toString()
    }

    override fun getItemCount() = users.size

}
