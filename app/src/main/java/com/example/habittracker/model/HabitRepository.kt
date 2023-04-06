package com.example.habittracker.model

import androidx.lifecycle.LiveData
import com.example.habittracker.entities.Habit
import com.example.habittracker.entities.SortType
import com.example.habittracker.viewmodel.Filter

class HabitRepository(private val habitDao: HabitDao) {

    var habits: LiveData<List<Habit>> = habitDao.getAll()

    fun createOrUpdate(habit: Habit) =
        habitDao.createOrUpdate(habit)

    fun delete(habit: Habit) =
        habitDao.delete(habit)

    fun getHabit(id: Long?): LiveData<Habit?> =
        habitDao.getHabit(id)

    fun applyFilters(filter: Filter): LiveData<List<Habit>> =
        when (filter.sortType) {
            SortType.CREATION_DATE_DESCENDING, SortType.CREATION_DATE_ASCENDING ->
                habitDao.getFilteredAndSortedByCreationDate(filter.priorities, filter.colors,
                    filter.sortType.isAsc, filter.searchQuery)
            SortType.PRIORITY_DESCENDING, SortType.PRIORITY_ASCENDING ->
                habitDao.getFilteredAndSortedByPriority(filter.priorities, filter.colors,
                    filter.sortType.isAsc, filter.searchQuery)
        }
}
