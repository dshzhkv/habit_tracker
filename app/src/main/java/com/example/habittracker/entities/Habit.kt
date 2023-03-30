package com.example.habittracker.entities

import java.util.Date

data class Habit(
    val id: Long,
    val title: String,
    val type: HabitType,
    val priority: HabitPriority,
    val repetitionTimes: Int,
    val repetitionPeriod: Period,
    val description: String?,
    val color: HabitColor,
    val creationDate: Date,
) : java.io.Serializable
