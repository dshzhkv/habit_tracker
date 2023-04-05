package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.view.MainActivity

class EditHabitViewModelFactory(private val habitId: Long?) : ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditHabitViewModel(MainActivity.model, habitId) as T
    }
}