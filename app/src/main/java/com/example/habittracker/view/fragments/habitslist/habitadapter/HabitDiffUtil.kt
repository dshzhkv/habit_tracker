package com.example.habittracker.view.fragments.habitslist.habitadapter

import androidx.recyclerview.widget.DiffUtil
import com.example.habittracker.entities.Habit

class HabitDiffUtil: DiffUtil.ItemCallback<Habit>() {
    override fun areItemsTheSame(oldItem: Habit, newItem: Habit) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Habit, newItem: Habit) =
        oldItem == newItem
}