package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.domain.entities.Habit
import com.example.domain.usecases.EditHabitUseCase


class EditHabitViewModel(private val editHabitUseCase: EditHabitUseCase) : ViewModel() {
    fun getHabit(habitId: String?): Habit? =
        editHabitUseCase.getHabit(habitId)

    fun createOrUpdateHabit(newHabit: Habit) =
        editHabitUseCase.createOrUpdateHabit(newHabit)

    fun deleteHabit(habitId: String) =
        editHabitUseCase.deleteHabit(habitId)

    fun checkHabit(habitId: String) =
        editHabitUseCase.checkHabit(habitId)

    fun isHabitDone(habit: Habit): Pair<Boolean, Int> =
        editHabitUseCase.isHabitDone(habit)
}