package com.example.habittracker.viewmodel


import androidx.lifecycle.*
import com.example.domain.entities.*
import com.example.domain.interactors.CheckHabitInteractor
import com.example.domain.interactors.FilterHabitsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class HabitsListViewModel(private val filterHabitsInteractor: FilterHabitsInteractor,
                          private val checkHabitInteractor: CheckHabitInteractor): ViewModel() {

    val filterFlow = MutableStateFlow(Filter(
        filterHabitsInteractor.habits,
        mutableSetOf(),
        mutableSetOf(),
        SortType.EDIT_DATE_DESCENDING,
        "",
    ))

    var habitsFlow: Flow<List<Habit>> = filterHabitsInteractor.habits

    init {
        resetFilters()

        habitsFlow = filterFlow.flatMapLatest { filter ->
            filterHabitsInteractor.applyFilters(filter)
        }

        getHabits()
    }

    private fun resetFilters() {
        filterFlow.value = filterHabitsInteractor.resetFilters(filterFlow)
    }

    private fun getHabits() =
        viewModelScope.launch(Dispatchers.IO) {
            filterHabitsInteractor.getHabits()
        }

    fun <T> filter(option: T, filterType: FilterType, filterAction: FilterAction) {
        filterFlow.value =
            filterHabitsInteractor.filter(option, filterType, filterAction, filterFlow)
    }

    fun search(query: String) {
        filterFlow.value = filterHabitsInteractor.search(filterFlow, query)
    }

    fun sortBy(sortType: SortType) {
        filterFlow.value = filterHabitsInteractor.sortBy(filterFlow, sortType)
    }

    fun getCheckedPriorities(): BooleanArray =
        filterHabitsInteractor.getCheckedPriorities(filterFlow)

    fun getCheckedColors(): BooleanArray =
        filterHabitsInteractor.getCheckedColors(filterFlow)

    fun checkHabit(habitId: String) =
        checkHabitInteractor.checkHabit(habitId)

    fun getHabitProgress(habitId: String) =
        checkHabitInteractor.getHabitProgress(habitId)
}
