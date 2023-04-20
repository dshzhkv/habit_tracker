package com.example.domain.entities

import com.example.domain.R

enum class Period(val textId: Int) {
    DAY(R.string.day),
    WEEK (R.string.week),
    MONTH (R.string.month),
    YEAR (R.string.year);
}