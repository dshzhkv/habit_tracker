package com.example.habittracker.entities

import com.example.habittracker.R


enum class SortType (val textId: Int, val isAsc: Boolean) {
    CREATION_DATE_DESCENDING (R.string.sort_by_creation_date_descending, false),
    CREATION_DATE_ASCENDING (R.string.sort_by_creation_date_ascending, true),
    PRIORITY_DESCENDING (R.string.sort_by_priority_descending, false),
    PRIORITY_ASCENDING (R.string.sort_by_priority_ascending, true),
}