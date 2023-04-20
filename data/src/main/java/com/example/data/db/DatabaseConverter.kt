package com.example.data.db

import androidx.room.TypeConverter
import com.example.domain.entities.HabitPriority
import java.util.*

class DatabaseConverter {
    @TypeConverter
    fun toDate(dateLong: Long): Date = Date(dateLong)

    @TypeConverter
    fun fromDate(date: Date): Long = date.time

    @TypeConverter
    fun toPriority(value: String): HabitPriority =
        HabitPriority.values().find { it.value.toString() == value } ?: HabitPriority.HIGH

    @TypeConverter
    fun fromPriority(value: HabitPriority): String = value.value.toString()

    @TypeConverter
    fun toDatesString(dates: String): List<Date> =
        if (dates.isEmpty()) listOf() else dates.split(";").map { Date(it.toLong()) }

    @TypeConverter
    fun fromListDate(dates: List<Date>): String =
        dates.joinToString(";") { it.time.toString() }
}