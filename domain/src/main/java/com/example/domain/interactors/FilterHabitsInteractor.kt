package com.example.domain.interactors

import com.example.domain.HabitRepository
import com.example.domain.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class FilterHabitsInteractor(private val repository: HabitRepository) {
    val habits = repository.habits

    suspend fun getHabits() =
        withContext(Dispatchers.IO) {
            repository.getHabitsFromServer()
        }

    fun applyFilters(filter: Filter): Flow<List<Habit>> =
        repository.applyFilters(filter)

    fun <T> filter(option: T, filterType: FilterType, filterAction: FilterAction,
                   filterFlow: MutableStateFlow<Filter>) : Filter =
        when (filterType) {
            FilterType.PRIORITY -> {
                val priorities = filterFlow.value.selectedPriorities.toMutableSet()
                when (filterAction) {
                    FilterAction.ADD -> priorities.add(option as HabitPriority)
                    FilterAction.REMOVE -> priorities.remove(option as HabitPriority)
                }
                updateFilterFlow(filterFlow, priorities = priorities)
            }
            FilterType.COLOR -> {
                val colors = filterFlow.value.selectedColors.toMutableSet()
                when (filterAction) {
                    FilterAction.ADD -> colors.add(option as HabitColor)
                    FilterAction.REMOVE -> colors.remove(option as HabitColor)
                }
                updateFilterFlow(filterFlow, colors = colors)
            }
        }

    private fun updateFilterFlow(filterFlow: MutableStateFlow<Filter>,
                                 priorities: Set<HabitPriority>? = null,
                                 colors: Set<HabitColor>? = null,
                                 sortType: SortType? = null,
                                 searchQuery: String? = null): Filter =
        Filter(
            habits,
            priorities ?: filterFlow.value.selectedPriorities,
            colors ?: filterFlow.value.selectedColors,
            sortType ?: filterFlow.value.sortType,
            searchQuery ?: filterFlow.value.searchQuery,
        )

    fun resetFilters(filterFlow: MutableStateFlow<Filter>) =
        updateFilterFlow(
            filterFlow,
            HabitPriority.values().toSet(),
            HabitColor.values().toSet(),
            SortType.EDIT_DATE_DESCENDING,
            "",
        )

    fun search(filterFlow: MutableStateFlow<Filter>, query: String): Filter {
        resetFilters(filterFlow)
        return updateFilterFlow(filterFlow, searchQuery = query)
    }

    fun sortBy(filterFlow: MutableStateFlow<Filter>, sortType: SortType): Filter {
        return updateFilterFlow(filterFlow, sortType = sortType)
    }

    private fun isFilteredByPriority(filterFlow: MutableStateFlow<Filter>,
                                     priority: HabitPriority): Boolean =
        filterFlow.value.selectedPriorities.contains(priority)

    private fun isFilteredByColor(filterFlow: MutableStateFlow<Filter>,
                                  color: HabitColor): Boolean =
        filterFlow.value.selectedColors.contains(color)

    fun getCheckedPriorities(filterFlow: MutableStateFlow<Filter>): BooleanArray =
        BooleanArray(HabitPriority.values().size) { index: Int ->
            isFilteredByPriority(
                filterFlow,
                HabitPriority.values()[index]
            )
        }

    fun getCheckedColors(filterFlow: MutableStateFlow<Filter>, ): BooleanArray =
        BooleanArray(HabitColor.values().size) { index: Int ->
            isFilteredByColor(
                filterFlow,
                HabitColor.values()[index]
            )
        }
}
