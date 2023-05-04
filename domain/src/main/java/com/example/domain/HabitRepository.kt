package com.example.domain

import com.example.domain.entities.Filter
import com.example.domain.entities.Habit
import com.example.domain.entities.HabitResponse
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface HabitRepository {

    val habits: Flow<List<Habit>>

    suspend fun getHabitsFromServer(): Pair<List<HabitResponse>?, List<Habit>?>

    suspend fun syncNotCreated(habit: Habit)

    suspend fun syncNotUpdated(localHabit: Habit, remoteHabit: HabitResponse)

    suspend fun syncNotDeleted(notDeleted: List<HabitResponse>)

    suspend fun syncChecks(habit: Habit, remoteHabit: HabitResponse?, id: String)

    fun updateDatabase(habitsFromServer: List<HabitResponse>)

    suspend fun createOrUpdate(habit: Habit)

    suspend fun delete(habitId: String)

    suspend fun checkHabit(date: Date, habitId: String)

    fun getHabit(id: String?): Habit?

    fun applyFilters(filter: Filter): Flow<List<Habit>>
}