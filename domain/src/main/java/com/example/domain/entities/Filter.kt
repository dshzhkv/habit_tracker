package com.example.domain.entities

import kotlinx.coroutines.flow.Flow

data class Filter(
    val habits: Flow<List<Habit>>,
    var selectedPriorities: Set<HabitPriority>,
    var selectedColors: Set<HabitColor>,
    var sortType: SortType,
    var searchQuery: String,
)