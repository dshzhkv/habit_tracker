package com.example.habittracker.entities

import com.example.habittracker.R

enum class SortType (val textId: Int) {
    CREATION_DATE_DESCENDING (R.string.sort_by_creation_date_descending),
    CREATION_DATE_ASCENDING (R.string.sort_by_creation_date_ascending),
    PRIORITY_DESCENDING (R.string.sort_by_priority_descending),
    PRIORITY_ASCENDING (R.string.sort_by_priority_ascending),
}