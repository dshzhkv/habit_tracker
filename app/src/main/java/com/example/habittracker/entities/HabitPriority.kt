package com.example.habittracker.entities

import com.example.habittracker.R

enum class HabitPriority(val colorId: Int, val fontColorId: Int, val textId: Int){
    HIGH(R.color.red, R.color.white, R.string.high_priority),
    MEDIUM(R.color.yellow, R.color.black, R.string.medium_priority),
    LOW(R.color.green, R.color.white, R.string.low_priority);
}