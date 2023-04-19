package com.example.habittracker.model

import androidx.lifecycle.LiveData
import com.example.habittracker.entities.DeleteHabitBody
import com.example.habittracker.entities.Habit
import com.example.habittracker.entities.SortType
import com.example.habittracker.viewmodel.Filter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitRepository(private val habitDao: HabitDao, private val service: HabitService) {

    val habits: LiveData<List<Habit>> = habitDao.getAll()

    suspend fun getHabitsFromServer() =
        withContext(Dispatchers.IO) {
            val habits = service.getAllHabits()
            habitDao.clear()
            habitDao.insert(habits)
    }

    suspend fun createOrUpdate(habit: Habit) =
        withContext(Dispatchers.IO) {
            service.addOrUpdateHabit(habit)
            getHabitsFromServer()
        }

    suspend fun delete(habit: Habit) =
        withContext(Dispatchers.IO) {
            service.deleteHabit(DeleteHabitBody(habit.id))
            getHabitsFromServer()
        }

    fun getHabit(id: String?): LiveData<Habit?> =
        habitDao.getHabit(id)

    fun applyFilters(filter: Filter): LiveData<List<Habit>> =
        when (filter.sortType) {
            SortType.EDIT_DATE_DESCENDING, SortType.EDIT_DATE_ASCENDING ->
                habitDao.getFilteredAndSortedByEditDate(filter.priorities, filter.colors,
                    filter.sortType.isAsc, filter.searchQuery)
            SortType.PRIORITY_DESCENDING, SortType.PRIORITY_ASCENDING ->
                habitDao.getFilteredAndSortedByPriority(filter.priorities, filter.colors,
                    filter.sortType.isAsc, filter.searchQuery)
        }
}
