package com.example.habittracker

import android.content.Context


enum class HabitPriority(val colorId: Int, val fontColorId: Int, val textId: Int){
    HIGH(R.color.red, R.color.white, R.string.high_priority),
    MEDIUM(R.color.yellow, R.color.black, R.string.medium_priority),
    LOW(R.color.green, R.color.white, R.string.low_priority);

    companion object {
        infix fun at (position: Int?): HabitPriority = HabitPriority.values()[position ?: 0]
    }
}

enum class HabitType(val colorId: Int, val fontColorId: Int, val textId: Int,
                     val radioButtonId: Int){
    BAD(R.color.red, R.color.white, R.string.bad_habit, R.id.bad_habit_button),
    GOOD(R.color.green, R.color.white, R.string.good_habit, R.id.good_habit_button);

    companion object {
        infix fun from(radioButtonId: Int?): HabitType =
            HabitType.values().firstOrNull { it.radioButtonId == radioButtonId } ?: GOOD
    }
}

enum class Period(val textId: Int) {
    DAY(R.string.day),
    WEEK (R.string.week),
    MONTH (R.string.month),
    QUARTER (R.string.quarter),
    YEAR (R.string.year);

    companion object {
        fun strings(context: Context): List<String> =
            Period.values().map { context.getString(it.textId) }

        infix fun at (position: Int?): Period = Period.values()[position ?: 0]
    }
}

data class Habit(
    val title: String,
    val type: HabitType,
    val priority: HabitPriority,
    val repetitionTimes: Int,
    val repetitionPeriod: Period,
    val description: String?,
    val colorId: Int
) : java.io.Serializable
