package com.example.habittracker.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.habittracker.entities.*
import com.example.habittracker.model.HabitRepository


data class Filter(
    val habits: LiveData<List<Habit>>,
    var priorities: MutableSet<HabitPriority>,
    var colors: MutableSet<HabitColor>,
    var sortType: SortType,
    var searchQuery: String,
)

class HabitsListViewModel(private val repository: HabitRepository): ViewModel() {
    private var _sortType: SortType = SortType.CREATION_DATE_DESCENDING
    private var _selectedPriorities: MutableSet<HabitPriority> = mutableSetOf()
    private var _selectedColors: MutableSet<HabitColor> = mutableSetOf()
    private var _searchQuery: String = ""

    private var filter: MutableLiveData<Filter> = MutableLiveData<Filter>(Filter(
        repository.habits,
        _selectedPriorities,
        _selectedColors,
        _sortType,
        _searchQuery,
    ))

    var habits: LiveData<List<Habit>> = repository.habits

    private var selectedPrioritiesMutableLiveData: MutableLiveData<MutableSet<HabitPriority>> = MutableLiveData(_selectedPriorities)
    private var selectedColorsMutableLiveData: MutableLiveData<MutableSet<HabitColor>> = MutableLiveData(_selectedColors)

    val selectedPriorities: LiveData<MutableSet<HabitPriority>> = selectedPrioritiesMutableLiveData
    val selectedColors: LiveData<MutableSet<HabitColor>> = selectedColorsMutableLiveData

    init {
        resetFilters()
        applyFilters()

        habits = Transformations.switchMap(filter) { filter ->
            repository.applyFilters(filter)
        }
    }

    fun <T> removeFromFilter(option: T, filterType: FilterType) {
        when (filterType) {
            FilterType.PRIORITY -> {
                _selectedPriorities.remove(option as HabitPriority)
                selectedPrioritiesMutableLiveData.value = _selectedPriorities
            }
            FilterType.COLOR -> {
                _selectedColors.remove(option as HabitColor)
                selectedColorsMutableLiveData.value = _selectedColors
            }
        }

        applyFilters()
    }

    fun <T> addToFilter(option: T, filterType: FilterType) {
        when (filterType) {
            FilterType.PRIORITY -> {
                _selectedPriorities.add(option as HabitPriority)
                selectedPrioritiesMutableLiveData.value = _selectedPriorities
            }
            FilterType.COLOR -> {
                _selectedColors.add(option as HabitColor)
                selectedColorsMutableLiveData.value = _selectedColors
            }
        }

        applyFilters()
    }

    fun search(query: String) {
        resetFilters()
        _searchQuery = query

        applyFilters()
    }

    fun sortBy(sortType: SortType) {
        _sortType = sortType

        applyFilters()
    }

    private fun resetFilters() {
        _sortType = SortType.CREATION_DATE_DESCENDING

        _selectedPriorities.addAll(HabitPriority.values())
        selectedPrioritiesMutableLiveData.value = _selectedPriorities

        _selectedColors.addAll(HabitColor.values())
        selectedColorsMutableLiveData.value = _selectedColors

        _searchQuery = ""
    }

    private fun applyFilters() {
        filter.value = Filter(
            repository.habits,
            _selectedPriorities,
            _selectedColors,
            _sortType,
            _searchQuery
        )
    }

    private fun isFilteredByPriority(priority: HabitPriority): Boolean =
        _selectedPriorities.contains(priority)

    private fun isFilteredByColor(color: HabitColor): Boolean =
        _selectedColors.contains(color)

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