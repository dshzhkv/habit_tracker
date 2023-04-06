package com.example.habittracker.view.fragments.habitslist.habitadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.ListAdapter
import com.example.habittracker.databinding.HabitCardBinding
import com.example.habittracker.entities.Habit

class HabitAdapter(private val navController: NavController)
    : ListAdapter<Habit, HabitViewHolder>(HabitDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = HabitCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding, navController)
    }
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
