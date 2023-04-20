package com.example.domain

import com.example.domain.entities.Filter
import com.example.domain.entities.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    val habits: Flow<List<Habit>>

    suspend fun getHabitsFromServer()

    suspend fun createOrUpdate(habit: Habit)

    suspend fun delete(habit: Habit)

    fun getHabit(id: String?): Flow<Habit?>

    fun applyFilters(filter: Filter): Flow<List<Habit>>
}