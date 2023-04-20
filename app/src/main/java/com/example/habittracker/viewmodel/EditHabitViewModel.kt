package com.example.habittracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.domain.entities.Habit
import com.example.domain.usecases.EditHabitUseCase


class EditHabitViewModel(private val editHabitUseCase: EditHabitUseCase, habitId: String?)
    : ViewModel() {

    val habit: LiveData<Habit?> = editHabitUseCase.loadHabit(habitId).asLiveData()

    fun createOrUpdateHabit(newHabit: Habit) =
        editHabitUseCase.createOrUpdateHabit(newHabit)

    fun deleteHabit(newHabit: Habit) =
        editHabitUseCase.deleteHabit(newHabit)
}