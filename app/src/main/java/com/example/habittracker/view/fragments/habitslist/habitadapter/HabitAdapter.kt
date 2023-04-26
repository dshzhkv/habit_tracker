package com.example.habittracker.view.fragments.habitslist.habitadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.ListAdapter
import com.example.habittracker.databinding.HabitCardBinding
import com.example.domain.entities.Habit
import com.example.habittracker.viewmodel.EditHabitViewModel

class HabitAdapter(private val navController: NavController,
                   private val viewModel: EditHabitViewModel)
    : ListAdapter<Habit, HabitViewHolder>(HabitDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = HabitCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding, navController, viewModel)
    }
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
