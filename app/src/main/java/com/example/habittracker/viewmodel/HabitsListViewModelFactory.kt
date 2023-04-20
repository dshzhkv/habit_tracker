package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.usecases.FilterHabitsUseCase

class HabitsListViewModelFactory(private val filterHabitsUseCase: FilterHabitsUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HabitsListViewModel(filterHabitsUseCase) as T
    }
}