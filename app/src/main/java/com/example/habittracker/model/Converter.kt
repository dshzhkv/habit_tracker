package com.example.habittracker.model

import androidx.room.TypeConverter
import com.example.habittracker.entities.HabitPriority
import java.util.*

class Converter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return if (dateLong == null) null else Date(dateLong)
    }
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toPriority(value: String) = HabitPriority.values().find { it.value.toString() == value } ?: HabitPriority.HIGH

    @TypeConverter
    fun fromPriority(value: HabitPriority) = value.value.toString()
}