package com.example.data.service

import com.example.data.entities.HabitDoneBody
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class HabitDoneBodySerializer: JsonSerializer<HabitDoneBody> {
    override fun serialize(
        src: HabitDoneBody,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement =
        JsonObject().apply {
            addProperty("date", src.date.time)
            addProperty("habit_uid", src.uid)
        }

}