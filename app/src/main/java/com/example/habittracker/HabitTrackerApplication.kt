package com.example.habittracker

import android.app.Application
import com.example.habittracker.model.HabitDatabase
import com.example.habittracker.model.HabitRepository

class HabitTrackerApplication : Application() {
    private val database by lazy { HabitDatabase.getDatabase(this) }
    val repository by lazy { HabitRepository(database.habitDao()) }
}