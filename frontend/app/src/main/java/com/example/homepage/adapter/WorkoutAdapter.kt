package com.example.homepage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homepage.R
import com.example.homepage.model.Workout

class WorkoutAdapter(
    private val listener: OnWorkoutActionListener
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    private val items = mutableListOf<Workout>()

    interface OnWorkoutActionListener {
        fun onLikeClicked(workout: Workout)
        fun onDislikeClicked(workout: Workout)
    }

    fun setData(newList: List<Workout>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    inner class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView   = view.findViewById(R.id.tvWorkoutName)
        val btnLike: Button    = view.findViewById(R.id.btnLike)
        val btnDislike: Button = view.findViewById(R.id.btnDislike)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = items[position]
        holder.tvName.text = workout.name

        holder.btnLike.setOnClickListener {
            listener.onLikeClicked(workout)
        }
        holder.btnDislike.setOnClickListener {
            listener.onDislikeClicked(workout)
        }
    }

    override fun getItemCount(): Int = items.size
}
