package com.example.domain.usecases

import com.example.domain.HabitRepository
import com.example.domain.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FilterHabitsUseCase(private val repository: HabitRepository) {
    val habits = repository.habits

    suspend fun getHabits() =
        withContext(Dispatchers.IO) {
            repository.getHabitsFromServer()
        }

    fun applyFilters(filter: Filter): Flow<List<Habit>> =
        repository.applyFilters(filter)
}