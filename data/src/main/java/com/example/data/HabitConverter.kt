package com.example.data

import com.example.domain.entities.HabitResponse
import com.example.domain.entities.*
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import java.util.*


fun Habit.toRequest(): HabitRequest {
    var habitRequest = HabitRequest(
        this.color.colorId,
        this.repetitionTimes,
        this.editDate.time,
        this.repetitionPeriod.ordinal,
        this.priority.value,
        this.title,
        this.type.ordinal,
    )

    if (this.id.isNotEmpty()) {
        habitRequest = habitRequest.copy(uid = this.id)
    }
    if (this.description != null && this.description.toString().isNotEmpty()) {
        habitRequest = habitRequest.copy(description = this.description.toString())
    }
    return habitRequest
}

fun HabitResponse.toLocal(): Habit {
    val description =
        if (this.description == EMPTY_DESCRIPTION) {
            null
        } else {
            this.description
        }
    val repetitionPeriod: Period = Period.values().getOrElse(this.frequency) { Period.DAY}
    val doneDates = this.done_dates.toDateList()
    val doneTimes = countDoneTimes(doneDates, repetitionPeriod)

    return Habit(
        this.title,
        HabitType.values().getOrElse(this.type) { HabitType.GOOD},
        HabitPriority.values().find { it.value == this.priority } ?: HabitPriority.HIGH,
        this.count,
        repetitionPeriod,
        description,
        HabitColor.values().find { it.colorId == this.color } ?: HabitColor.GRADIENT_COLOR1,
        Date(this.date),
        doneDates,
        doneTimes,
        true,
        this.uid,
    )
}

fun JsonArray.toDateList(): List<Date> =
    Gson().fromJson<List<Long>>(this, object : TypeToken<List<Long>>() {}.type).map { Date(it) }

fun countDoneTimes(doneDates: List<Date>, repetitionPeriod: Period): Int {
    val calendar = Calendar.getInstance()

    val period: Pair<Date, Date> = when (repetitionPeriod) {
        Period.DAY -> {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            val start = calendar.time
            calendar.add(Calendar.DATE, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            Pair(start, calendar.time)
        }
        Period.WEEK -> {
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            val start = calendar.time
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            Pair(start, calendar.time)
        }
        Period.MONTH -> {
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            val start = calendar.time
            calendar.add(Calendar.MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            Pair(start, calendar.time)
        }
        Period.YEAR -> {
            calendar.set(calendar.get(Calendar.YEAR), 0, 1)
            val start = calendar.time
            calendar.set(calendar.get(Calendar.YEAR), 11, 31)
            Pair(start, calendar.time)
        }
    }
    val (periodStart, periodEnd) = period
    val datesInPeriod = doneDates.filter { it >= periodStart && it < periodEnd }
    return datesInPeriod.size
}