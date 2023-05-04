package com.example.domain.entities

import com.google.gson.JsonArray

data class HabitResponse(
    val color: Int,
    val count: Int,
    val date: Long,
    val description: String,
    val done_dates: JsonArray,
    val frequency: Int,
    val priority: Int,
    val title: String,
    val type: Int,
    val uid: String,
)
