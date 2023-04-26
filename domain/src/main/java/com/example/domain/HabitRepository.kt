package com.example.domain

import com.example.domain.entities.Filter
import com.example.domain.entities.Habit
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface HabitRepository {

    val habits: Flow<List<Habit>>

    suspend fun getHabitsFromServer()

    suspend fun createOrUpdate(habit: Habit)

    suspend fun delete(habitId: String)

    suspend fun checkHabit(date: Date, habitId: String)

    fun getHabit(id: String?): Habit?

    fun applyFilters(filter: Filter): Flow<List<Habit>>
}