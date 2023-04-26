package com.example.data.service

import com.example.domain.entities.*
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


private const val EMPTY_DESCRIPTION = "empty_description"

class HabitJsonSerializer: JsonSerializer<Habit> {
    override fun serialize(
        src: Habit,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement =
        JsonObject().apply {
            addProperty("color", src.color.colorId)
            addProperty("count", src.repetitionTimes)
            addProperty("date", src.editDate.time)
            if (src.description == null || src.description.toString().isEmpty()) {
                addProperty("description", EMPTY_DESCRIPTION)
            } else {
                addProperty("description", src.description)
            }

            val doneDatesJson = JsonArray()
            src.doneDates.forEach { doneDatesJson.add(it.time) }
            add("done_dates", doneDatesJson)

            addProperty("frequency", src.repetitionPeriod.ordinal)
            addProperty("priority", src.priority.value)
            addProperty("title", src.title)
            addProperty("type", src.type.ordinal)
            if (src.id.isNotEmpty()) {
                addProperty("uid", src.id)
            }
        }
}

class HabitJsonDeserializer: JsonDeserializer<Habit> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Habit {
        val jsonObject = json.asJsonObject
        val description =
            if (jsonObject.get("description").asString == EMPTY_DESCRIPTION) {
                null
            } else {
                jsonObject.get("description").asString
            }
        val repetitionPeriod: Period = Period.values().getOrElse(jsonObject.get("frequency").asInt) {Period.DAY}
        val doneDates =  Gson().fromJson<List<Long>>(jsonObject.get("done_dates"), object : TypeToken<List<Long>>() {}.type).map { Date(it) }
        val doneTimes = countDoneTimes(doneDates, repetitionPeriod)

        return Habit(
            jsonObject.get("title").asString,
            HabitType.values().getOrElse(jsonObject.get("type").asInt) {HabitType.GOOD},
            HabitPriority.values().find { it.value == jsonObject.get("priority").asInt } ?: HabitPriority.HIGH,
            jsonObject.get("count").asInt,
            repetitionPeriod,
            description,
            HabitColor.values().find { it.colorId == jsonObject.get("color").asInt } ?: HabitColor.GRADIENT_COLOR1,
            Date(jsonObject.get("date").asLong),
            doneDates,
            doneTimes,
            jsonObject.get("uid").asString,
        )
    }

    private fun countDoneTimes(doneDates: List<Date>, repetitionPeriod: Period): Int {
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
}



