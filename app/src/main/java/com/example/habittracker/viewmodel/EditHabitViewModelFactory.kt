package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.interactors.EditHabitInteractor

class EditHabitViewModelFactory(private val editHabitInteractor: EditHabitInteractor)
    : ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditHabitViewModel(editHabitInteractor) as T
    }
}