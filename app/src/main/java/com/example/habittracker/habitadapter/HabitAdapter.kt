package com.example.habittracker.habitadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.habittracker.R
import com.example.habittracker.entities.Habit

class HabitAdapter(private val habits: List<Habit>,
                   private val context: Context,
                   private val onHabitCardListener: OnHabitCardListener
)
    : ListAdapter<Habit, HabitViewHolder>(HabitDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.habit_card, parent,
            false
        )
        return HabitViewHolder(itemView, onHabitCardListener, context)
    }

    override fun getItemCount(): Int {
        return habits.size
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }
}
