package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.interactors.CheckHabitInteractor
import com.example.domain.interactors.FilterHabitsInteractor

class HabitsListViewModelFactory(private val filterHabitsInteractor: FilterHabitsInteractor,
                                 private val checkHabitInteractor: CheckHabitInteractor) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HabitsListViewModel(filterHabitsInteractor, checkHabitInteractor) as T
    }
}