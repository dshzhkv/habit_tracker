package com.example.domain.entities

import androidx.lifecycle.LiveData

data class Filter(
    val habits: LiveData<List<Habit>>,
    var priorities: MutableSet<HabitPriority>,
    var colors: MutableSet<HabitColor>,
    var sortType: SortType,
    var searchQuery: String,
)