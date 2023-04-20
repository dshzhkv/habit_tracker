package com.example.data.repository

import com.example.data.db.HabitDao
import com.example.data.entities.DeleteHabitBody
import com.example.data.service.HabitService
import com.example.domain.HabitRepository
import com.example.domain.entities.Filter
import com.example.domain.entities.Habit
import com.example.domain.entities.SortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HabitRepositoryImpl(private val habitDao: HabitDao, private val service: HabitService): HabitRepository {

    override val habits: Flow<List<Habit>> = habitDao.getAll()

    override suspend fun getHabitsFromServer() =
        withContext(Dispatchers.IO) {
            val habits = service.getAllHabits()
            habitDao.clear()
            habitDao.insert(habits)
    }

    override suspend fun createOrUpdate(habit: Habit) =
        withContext(Dispatchers.IO) {
            service.addOrUpdateHabit(habit)
            getHabitsFromServer()
        }

    override suspend fun delete(habit: Habit) =
        withContext(Dispatchers.IO) {
            service.deleteHabit(DeleteHabitBody(habit.id))
            getHabitsFromServer()
        }

    override fun getHabit(id: String?): Flow<Habit?> =
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
