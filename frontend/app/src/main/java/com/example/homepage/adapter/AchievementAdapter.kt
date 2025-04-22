package com.example.homepage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homepage.R
import com.example.homepage.ui.Achievement
import com.example.homepage.ui.AchievementModel

class AchievementAdapter(private val achievements: List<AchievementModel>) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    inner class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvAchievementName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvAchievementDescription)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val tvProgressText: TextView = itemView.findViewById(R.id.tvProgressText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievements, parent, false)
        return AchievementViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.tvName.text = achievement.name
        holder.tvDescription.text = achievement.description

        holder.progressBar.max = achievement.requiredProgress
        holder.progressBar.progress = achievement.progress
        holder.tvProgressText.text = "${achievement.progress} / ${achievement.requiredProgress}"
    }

    override fun getItemCount(): Int = achievements.size


}