package com.example.habittracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittracker.entities.Habit
import com.example.habittracker.model.HabitRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class EditHabitViewModel(private val repository: HabitRepository, private val habitId: Long?)
    : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler {
                _, throwable -> throw throwable
        }

    private var mutableHabit: MutableLiveData<Habit?> = MutableLiveData()

    val habit: LiveData<Habit?> = repository.getHabit(habitId)

    init {
        loadHabit()
    }

    private fun loadHabit() {
        mutableHabit = MutableLiveData(repository.getHabit(habitId).value)
    }

    fun createOrUpdateHabit(newHabit: Habit) =
        launch { repository.createOrUpdate(newHabit) }


    fun deleteHabit(newHabit: Habit) =
        launch { repository.delete(newHabit) }
}