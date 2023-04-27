package com.example.data.repository

import com.example.data.db.HabitDao
import com.example.data.entities.DeleteHabitBody
import com.example.data.entities.HabitDoneBody
import com.example.data.service.HabitService
import com.example.domain.HabitRepository
import com.example.domain.entities.Filter
import com.example.domain.entities.Habit
import com.example.domain.entities.SortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*

class HabitRepositoryImpl(private val habitDao: HabitDao, private val service: HabitService): HabitRepository {

    override val habits: Flow<List<Habit>> = habitDao.getAll()

    override suspend fun getHabitsFromServer() =
        withContext(Dispatchers.IO) {
            try {
                val habitsFromServer = service.getAllHabits()
                val localHabits = habitDao.getAll().first()

                val notSyncedHabits = localHabits.filter { !it.isSynced }
                if (notSyncedHabits.isEmpty() && habitsFromServer.size == localHabits.size) {
                    habitDao.clear()
                    habitDao.insert(habitsFromServer)
                } else {
                    notSyncedHabits.forEach {
                        try {
                            createOrUpdate(it.copy(id = ""))
                            habitDao.delete(it.id)
                        } catch (_: java.lang.Exception) { }
                    }
                    habitsFromServer.filter { !localHabits.contains(it) }.forEach { delete(it.id) }
                }
            } catch (_: Exception) {}
    }

    override suspend fun createOrUpdate(habit: Habit): Unit =
        withContext(Dispatchers.IO) {
            try {
                val response = service.addOrUpdateHabit(habit)
                habitDao.createOrUpdate(habit.copy(id = response.uid, isSynced = true))
            } catch (_: Exception) {
                if (habit.id == "") {
                    habitDao.createOrUpdate(habit.copy(id = generateId(), isSynced = false))
                } else {
                    habitDao.createOrUpdate(habit.copy(isSynced = false))
                }
            }
        }

    private suspend fun generateId(): String =
        habitDao.getAll().first().filter { !it.isSynced }.size.toString()

    override suspend fun delete(habitId: String) =
        withContext(Dispatchers.IO) {
            habitDao.delete(habitId)
            try {
                service.deleteHabit(DeleteHabitBody(habitId))
            } catch (_: Exception) {}
        }

    override suspend fun checkHabit(date: Date, habitId: String) =
        withContext(Dispatchers.IO) {
            service.checkHabit(HabitDoneBody(date, habitId))
            val updatedHabit = service.getAllHabits().find { it.id == habitId }
            if (updatedHabit != null) {
                habitDao.createOrUpdate(updatedHabit)
            }
        }

    override fun getHabit(id: String?): Habit? =
        habitDao.getHabit(id)

    override fun applyFilters(filter: Filter): Flow<List<Habit>> =
        when (filter.sortType) {
            SortType.EDIT_DATE_DESCENDING, SortType.EDIT_DATE_ASCENDING ->
                habitDao.getFilteredAndSortedByEditDate(filter.priorities, filter.colors,
                    filter.sortType.isAsc, filter.searchQuery)
            SortType.PRIORITY_DESCENDING, SortType.PRIORITY_ASCENDING ->
                habitDao.getFilteredAndSortedByPriority(filter.priorities, filter.colors,
                    filter.sortType.isAsc, filter.searchQuery)
        }
}
