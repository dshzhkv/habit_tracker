package com.example.data.service

import com.example.domain.entities.Habit
import com.google.gson.*
import java.lang.reflect.Type

const val EMPTY_DESCRIPTION = "empty_description"

class HabitSerializer: JsonSerializer<Habit> {
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