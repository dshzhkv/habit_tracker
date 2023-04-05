package com.example.habittracker.model

import com.example.habittracker.entities.Habit
import com.example.habittracker.entities.HabitType

class HabitsListModel {

    private var habits: MutableList<Habit> = mutableListOf()

    fun getHabits(type: HabitType): List<Habit> {
        return habits.filter { habit -> habit.type == type }
    }

    fun getHabit(id: Long?): Habit? {
        return habits.firstOrNull { habit -> habit.id == id }
    }

    fun createOrUpdateHabit(id: Long?, newHabit: Habit) {
        if (id !== null && id >= 0) {
            editHabit(id, newHabit)
        } else {
            addHabit(newHabit)
        }
    }

    private fun editHabit(id: Long, newHabit: Habit) {
        val index = habits.indexOfFirst { habit -> habit.id == id }
        if (index >= 0) {
            habits[index] = newHabit
        }
    }

    private fun addHabit(habit: Habit) {
        val newHabit = Habit(
            habits.size.toLong(),
            habit.title,
            habit.type,
            habit.priority,
            habit.repetitionTimes,
            habit.repetitionPeriod,
            habit.description,
            habit.color,
            habit.creationDate
        )
        habits.add(newHabit)
    }
}