package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.domain.entities.Habit
import com.example.domain.interactors.EditHabitInteractor


class EditHabitViewModel(private val editHabitInteractor: EditHabitInteractor) : ViewModel() {
    fun getHabit(habitId: String?): Habit? =
        editHabitInteractor.getHabit(habitId)

    fun createOrUpdateHabit(newHabit: Habit) =
        editHabitInteractor.createOrUpdateHabit(newHabit)

    fun deleteHabit(habitId: String) =
        editHabitInteractor.deleteHabit(habitId)
}