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
                        val remoteHabit = habitsFromServer.find { habit -> habit.id == it.id }
                        if (remoteHabit != null) {
                            syncNotUpdated(it, remoteHabit)
                        } else {
                            syncNotCreated(it)
                        }
                    }
                    syncNotDeleted(habitsFromServer.filter { isHabitDeletedLocally(it.id, localHabits) })
                }
            } catch (_: Exception) {}
    }

    private suspend fun syncNotCreated(habit: Habit) {
        val response = service.addOrUpdateHabit(habit.copy(id = ""))
        habitDao.createOrUpdate(habit.copy(id = response.uid, isSynced = false))
        habitDao.delete(habit.id)
        syncChecks(habit, null, response.uid)
        habitDao.createOrUpdate(habit.copy(id = response.uid, isSynced = true))
    }

    private suspend fun syncNotUpdated(localHabit: Habit, remoteHabit: Habit) {
        if (localHabit.editDate > remoteHabit.editDate) {
            service.addOrUpdateHabit(localHabit)
        }
        if (localHabit.doneDates.size != remoteHabit.doneDates.size) {
            syncChecks(localHabit, remoteHabit, localHabit.id)
        }
        habitDao.createOrUpdate(localHabit.copy(isSynced = true))
    }

    private suspend fun syncChecks(habit: Habit, remoteHabit: Habit?, id: String) =
        habit.doneDates.forEach {
            if (remoteHabit == null || !remoteHabit.doneDates.contains(it)) {
                service.checkHabit(HabitDoneBody(it, id))
            }
        }

    private suspend fun syncNotDeleted(notDeleted: List<Habit>) =
        notDeleted.forEach { service.deleteHabit(DeleteHabitBody(it.id)) }


    private fun isHabitDeletedLocally(habitId: String, localHabits: List<Habit>) =
        localHabits.find { it.id == habitId } == null

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
        withContext(Dispatchers.IO) {
            habitDao.getAll().first().filter { !it.isSynced }.size.toString()
        }

    override suspend fun delete(habitId: String) =
        withContext(Dispatchers.IO) {
            habitDao.delete(habitId)
            try {
                service.deleteHabit(DeleteHabitBody(habitId))
            } catch (_: Exception) {}
        }

    override suspend fun checkHabit(date: Date, habitId: String) =
        withContext(Dispatchers.IO) {
            try {
                service.checkHabit(HabitDoneBody(date, habitId))
                checkHabitLocally(date, habitId, true)
            } catch (_: Exception) {
                checkHabitLocally(date, habitId, false)
            }
        }

    private suspend fun checkHabitLocally(date: Date, habitId: String, isSynced: Boolean) {
        withContext(Dispatchers.IO) {
            val habit = getHabit(habitId)
            if (habit != null) {
                val doneDates = habit.doneDates.toMutableList()
                doneDates.add(date)
                habitDao.createOrUpdate(habit.copy(doneDates = doneDates,
                    doneTimes = doneDates.size, isSynced = isSynced))
            }
        }
    }

    override fun getHabit(id: String?): Habit? =
        habitDao.getHabit(id)

    override fun applyFilters(filter: Filter): Flow<List<Habit>> =
        when (filter.sortType) {
            SortType.EDIT_DATE_DESCENDING, SortType.EDIT_DATE_ASCENDING ->
                habitDao.getFilteredAndSortedByEditDate(filter.selectedPriorities, filter.selectedColors,
                    filter.sortType.isAsc, filter.searchQuery)
            SortType.PRIORITY_DESCENDING, SortType.PRIORITY_ASCENDING ->
                habitDao.getFilteredAndSortedByPriority(filter.selectedPriorities, filter.selectedColors,
                    filter.sortType.isAsc, filter.searchQuery)
        }
}
