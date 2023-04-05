package com.example.habittracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittracker.entities.*
import com.example.habittracker.model.HabitsListModel

class HabitsListViewModel(private val model: HabitsListModel): ViewModel() {

    private var _habitsMap: MutableMap<HabitType, List<Habit>> = mutableMapOf()

    private var _sortType: SortType = SortType.CREATION_DATE_DESCENDING
    private var _selectedPriorities: MutableSet<HabitPriority> = mutableSetOf()
    private var _selectedColors: MutableSet<HabitColor> = mutableSetOf()
    private var _searchQuery: String = ""

    private var selectedPrioritiesMutableLiveData: MutableLiveData<MutableSet<HabitPriority>> = MutableLiveData(_selectedPriorities)
    private var selectedColorsMutableLiveData: MutableLiveData<MutableSet<HabitColor>> = MutableLiveData(_selectedColors)
    private var habitsMutableLiveData: MutableLiveData<Map<HabitType, List<Habit>>> = MutableLiveData(_habitsMap)

    val habits: LiveData<Map<HabitType, List<Habit>>> = habitsMutableLiveData
    val selectedPriorities: LiveData<MutableSet<HabitPriority>> = selectedPrioritiesMutableLiveData
    val selectedColors: LiveData<MutableSet<HabitColor>> = selectedColorsMutableLiveData

    init {
        HabitType.values().map { _habitsMap[it] = listOf() }
        habitsMutableLiveData.value = _habitsMap

        resetFilters()
    }

    fun loadHabits() {
        for (type in _habitsMap.keys) {
            _habitsMap[type] = model.getHabits(type).applyAllFilters()
        }
        habitsMutableLiveData.value = _habitsMap
    }

    fun <T> removeFromFilter(option: T, filterType: FilterType) {
        when (filterType) {
            FilterType.PRIORITY -> {
                val priority = option as HabitPriority
                _selectedPriorities.remove(priority)
                selectedPrioritiesMutableLiveData.value = _selectedPriorities
                removeFromFilter { habit -> isFilteredByPriority(habit.priority) }
            }
            FilterType.COLOR -> {
                val color = option as HabitColor
                _selectedColors.remove(color)
                selectedColorsMutableLiveData.value = _selectedColors
                removeFromFilter { habit -> isFilteredByColor(habit.color) }
            }
        }
    }

    private fun removeFromFilter(isFiltered: (habit: Habit) -> Boolean) {
        for (habitsList in _habitsMap.entries) {
            val filteredHabits: MutableList<Habit> = mutableListOf()
            habitsList.value.map {
                if (isFiltered(it)) {
                    filteredHabits.add(it)
                }
            }
            _habitsMap[habitsList.key] = filteredHabits
        }

        habitsMutableLiveData.value = _habitsMap
    }

    fun <T> addToFilter(option: T, filterType: FilterType) {
        when (filterType) {
            FilterType.PRIORITY -> {
                val priority = option as HabitPriority
                _selectedPriorities.add(priority)
                selectedPrioritiesMutableLiveData.value = _selectedPriorities
                addToFilter()
            }
            FilterType.COLOR -> {
                val color = option as HabitColor
                _selectedColors.add(color)
                selectedColorsMutableLiveData.value = _selectedColors
                addToFilter()
            }
        }
    }

    private fun addToFilter() {
        for (habitsList in _habitsMap.entries) {
            _habitsMap[habitsList.key] = model.getHabits(habitsList.key).applyAllFilters()
        }

        habitsMutableLiveData.value = _habitsMap
    }

    fun search(query: String) {
        resetFilters()
        _searchQuery = query

        for (habitsList in _habitsMap.entries) {
            val type = habitsList.key
            _habitsMap[type] =  model.getHabits(type).filter { isFilteredBySearch(it.title) }
        }

        habitsMutableLiveData.value = _habitsMap
    }

    fun sortBy(sortType: SortType) {
        for (habitsList in _habitsMap.entries) {
            _habitsMap[habitsList.key] = habitsList.value.sort(sortType)
        }
        habitsMutableLiveData.value = _habitsMap
    }

    private fun resetFilters() {
        _sortType = SortType.CREATION_DATE_DESCENDING

        _selectedPriorities.addAll(HabitPriority.values())
        selectedPrioritiesMutableLiveData.value = _selectedPriorities

        _selectedColors.addAll(HabitColor.values())
        selectedColorsMutableLiveData.value = _selectedColors

        _searchQuery = ""
    }

    private fun List<Habit>.applyAllFilters(): List<Habit> =
        this.filter { isFilteredByPriority(it.priority) && isFilteredBySearch(it.title) && isFilteredByColor(it.color)}.sort(_sortType)

    private fun isFilteredByPriority(priority: HabitPriority): Boolean =
        _selectedPriorities.contains(priority)

    private fun isFilteredByColor(color: HabitColor): Boolean =
        _selectedColors.contains(color)

    private fun isFilteredBySearch(title: String): Boolean =
        title.contains(_searchQuery, true)

    private fun List<Habit>.sort(sortType: SortType): List<Habit> =
        when(sortType) {
            SortType.PRIORITY_ASCENDING -> this.sortedBy { it.priority.value }
            SortType.PRIORITY_DESCENDING -> this.sortedByDescending { it.priority.value }
            SortType.CREATION_DATE_ASCENDING -> this.sortedBy { it.creationDate }
            SortType.CREATION_DATE_DESCENDING -> this.sortedByDescending { it.creationDate }
        }

    fun getCheckedPriorities(): BooleanArray =
        BooleanArray(HabitPriority.values().size) { index: Int ->
            isFilteredByPriority(
                HabitPriority.values()[index]
            )
        }

    fun getCheckedColors(): BooleanArray =
        BooleanArray(HabitColor.values().size) { index: Int ->
            isFilteredByColor(
                HabitColor.values()[index]
            )
        }
}