package com.example.domain.entities

import com.example.domain.R

enum class HabitType(val colorId: Int, val fontColorId: Int, val textId: Int){
    GOOD(R.color.green, R.color.white, R.string.good_habit),
    BAD(R.color.red, R.color.white, R.string.bad_habit)
}