package com.example.domain.usecases

import com.example.domain.HabitRepository
import com.example.domain.entities.Habit
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

class EditHabitUseCase(private val repository: HabitRepository): CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler {
                _, throwable -> throw throwable
        }

    fun loadHabit(habitId: String?): Flow<Habit?> =
        repository.getHabit(habitId)

    fun createOrUpdateHabit(newHabit: Habit) =
        launch { repository.createOrUpdate(newHabit) }

    fun deleteHabit(newHabit: Habit) =
        launch { repository.delete(newHabit) }
}