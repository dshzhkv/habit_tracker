package com.example.habittracker.entities

import com.example.habittracker.R

enum class Period(val textId: Int) {
    DAY(R.string.day),
    WEEK (R.string.week),
    MONTH (R.string.month),
    QUARTER (R.string.quarter),
    YEAR (R.string.year);
}