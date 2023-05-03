package com.example.domain.interactors

import com.example.domain.HabitRepository
import com.example.domain.entities.Habit
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class EditHabitInteractor(private val repository: HabitRepository): CoroutineScope {

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
}