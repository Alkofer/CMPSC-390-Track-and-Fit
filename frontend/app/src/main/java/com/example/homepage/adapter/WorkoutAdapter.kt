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
    private var workouts: List<Workout>,
    private val listener: OnWorkoutActionListener
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    interface OnWorkoutActionListener {
        fun onLikeClicked(workout: Workout)
        fun onDislikeClicked(workout: Workout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(workouts[position], listener)
    }

    override fun getItemCount(): Int = workouts.size

    fun setData(newWorkouts: List<Workout>) {
        workouts = newWorkouts
        notifyDataSetChanged()
    }

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvWorkoutName: TextView = itemView.findViewById(R.id.tvWorkoutName)
        private val btnLike: Button = itemView.findViewById(R.id.btnLike)
        private val btnDislike: Button = itemView.findViewById(R.id.btnDislike)

        fun bind(workout: Workout, listener: OnWorkoutActionListener) {
            tvWorkoutName.text = workout.name

            btnLike.setOnClickListener {
                listener.onLikeClicked(workout)
            }

            btnDislike.setOnClickListener {
                listener.onDislikeClicked(workout)
            }
        }
    }
}