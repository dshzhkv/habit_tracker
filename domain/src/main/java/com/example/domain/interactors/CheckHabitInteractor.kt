package com.example.domain.interactors

import com.example.domain.HabitRepository
import com.example.domain.entities.CheckHabitMessage
import com.example.domain.entities.HabitType
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class CheckHabitInteractor(private val repository: HabitRepository): CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler {
                _, throwable -> throw throwable
        }

    fun checkHabit(habitId: String) =
        launch { repository.checkHabit(Date(), habitId) }

    fun getHabitProgress(habitId: String): Pair<CheckHabitMessage, Int?> {
        val habit = repository.getHabit(habitId)
        habit ?: return Pair(CheckHabitMessage.ERROR, null)

        val doneTimes = habit.doneTimes + 1
        val message = when (isHabitDone(doneTimes, habit.repetitionTimes, habit.type)) {
            true -> {
                when (habit.type) {
                    HabitType.GOOD -> CheckHabitMessage.GOOD_DONE
                    HabitType.BAD -> CheckHabitMessage.BAD_NOT_OK
                }
            }
            false -> {
                when (habit.type) {
                    HabitType.GOOD -> CheckHabitMessage.GOOD_NOT_DONE
                    HabitType.BAD -> CheckHabitMessage.BAD_OK
                }
            }
        }
        return Pair(message, doneTimes)
    }

    private fun isHabitDone(doneTimes: Int, repetitionTimes: Int, habitType: HabitType): Boolean =
        when (habitType) {
            HabitType.GOOD -> doneTimes >= repetitionTimes
            HabitType.BAD -> doneTimes > repetitionTimes
    }
}