package com.example.habittracker.model

import com.example.habittracker.entities.*
import com.google.gson.*
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
            addProperty("date", src.creationDate.time)
            if (src.description == null || src.description.isEmpty()) {
                addProperty("description", EMPTY_DESCRIPTION)
            } else {
                addProperty("description", src.description)
            }
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
        return Habit(
            jsonObject.get("title").asString,
            HabitType.values().getOrElse(jsonObject.get("type").asInt) {HabitType.GOOD},
            HabitPriority.values().find { it.value == jsonObject.get("priority").asInt } ?: HabitPriority.HIGH,
            jsonObject.get("count").asInt,
            Period.values().getOrElse(jsonObject.get("frequency").asInt) {Period.DAY},
            description,
            HabitColor.values().find { it.colorId == jsonObject.get("color").asInt } ?: HabitColor.GRADIENT_COLOR1,
            Date(jsonObject.get("date").asLong),
            jsonObject.get("uid").asString,
        )
    }
}

