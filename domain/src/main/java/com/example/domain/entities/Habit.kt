package com.example.domain.entities

import androidx.room.*
import java.util.*

@Entity
data class Habit(
    val title: String,
    val type: HabitType,
    val priority: HabitPriority,
    val repetitionTimes: Int,
    val repetitionPeriod: Period,
    val description: String?,
    val color: HabitColor,
    val editDate: Date,
    val doneDates: List<Date>,
    val doneTimes: Int,
    @PrimaryKey val id: String = "",
) : java.io.Serializable
