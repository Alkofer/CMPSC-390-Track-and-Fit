package com.example.homepage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homepage.R
import com.example.homepage.model.Workout


class BlacklistAdapter(
    private val workouts: List<Workout>,
    private val onRemoveClicked: (Workout) -> Unit
) : RecyclerView.Adapter<BlacklistAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val workoutName: TextView = view.findViewById(R.id.tvWorkoutName)
        val muscleGroup: TextView = view.findViewById(R.id.tvMuscleGroup)
        val removeBtn: Button = view.findViewById(R.id.btnRemove)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blacklisted_workout, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int = workouts.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workout = workouts[position]
        holder.workoutName.text = workout.name
        holder.muscleGroup.text = workout.muscleGroup
        holder.removeBtn.setOnClickListener {
            onRemoveClicked(workout)
        }
    }
}
