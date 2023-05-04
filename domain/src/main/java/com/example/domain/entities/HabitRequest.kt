package com.example.domain.entities

const val EMPTY_DESCRIPTION = "empty_description"

data class HabitRequest(
    val color: Int,
    val count: Int,
    val date: Long,
    val frequency: Int,
    val priority: Int,
    val title: String,
    val type: Int,
    val description: String = EMPTY_DESCRIPTION,
    val uid: String? = null,
)
