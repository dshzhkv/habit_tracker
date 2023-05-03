package com.example.domain.entities

import com.example.domain.R

enum class CheckHabitMessage(val messageId: Int) {
    GOOD_DONE(R.string.good_habit_done),
    GOOD_NOT_DONE(R.string.good_habit_not_done),
    BAD_NOT_OK(R.string.bad_habit_not_ok),
    BAD_OK(R.string.bad_habit_ok),
    ERROR(R.string.check_habit_error),
}