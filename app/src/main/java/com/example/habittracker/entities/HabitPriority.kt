package com.example.habittracker.entities

import com.example.habittracker.R

enum class HabitPriority(val colorId: Int, val fontColorId: Int, val textId: Int, val value: Int){
    HIGH(R.color.red, R.color.white, R.string.high_priority, 2),
    MEDIUM(R.color.yellow, R.color.black, R.string.medium_priority, 1),
    LOW(R.color.green, R.color.white, R.string.low_priority, 0)
}