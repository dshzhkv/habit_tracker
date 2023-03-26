package com.example.habittracker.habitadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.ListAdapter
import com.example.habittracker.R
import com.example.habittracker.entities.Habit
import com.example.habittracker.entities.HabitType

class HabitAdapter(private val habits: List<Habit>,
                   private val type: HabitType,
                   private val context: Context,
                   private val navController: NavController
)
    : ListAdapter<Habit, HabitViewHolder>(HabitDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.habit_card, parent,
            false
        )
        return HabitViewHolder(itemView, context, navController, type)
    }

    override fun getItemCount(): Int {
        return habits.size
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }
}
