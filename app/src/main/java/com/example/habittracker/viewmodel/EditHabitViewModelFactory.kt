package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.usecases.EditHabitUseCase

class EditHabitViewModelFactory(private val editHabitUseCase: EditHabitUseCase,
                                private val habitId: String?)
    : ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditHabitViewModel(editHabitUseCase, habitId) as T
    }
}