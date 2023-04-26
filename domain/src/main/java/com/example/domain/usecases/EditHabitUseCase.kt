package com.example.domain.usecases

import com.example.domain.HabitRepository
import com.example.domain.entities.Habit
import com.example.domain.entities.HabitType
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class EditHabitUseCase(private val repository: HabitRepository): CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler {
                _, throwable -> throw throwable
        }

    fun getHabit(habitId: String?): Habit? =
        repository.getHabit(habitId)

    fun createOrUpdateHabit(newHabit: Habit) =
        launch { repository.createOrUpdate(newHabit) }

    fun deleteHabit(habitId: String) =
        launch { repository.delete(habitId) }

    fun checkHabit(habitId: String) =
        launch { repository.checkHabit(Date(), habitId) }

    fun isHabitDone(habit: Habit): Pair<Boolean, Int> {
        return Pair(
            when (habit.type) {
                HabitType.GOOD -> habit.doneTimes + 1 >= habit.repetitionTimes
                HabitType.BAD -> habit.doneTimes + 1 > habit.repetitionTimes
            }, habit.doneTimes + 1)
    }
}