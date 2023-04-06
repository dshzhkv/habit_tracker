package com.example.habittracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittracker.entities.Habit
import com.example.habittracker.model.HabitRepository


class EditHabitViewModel(private val repository: HabitRepository, private val habitId: Long?) : ViewModel() {

    private var mutableHabit: MutableLiveData<Habit?> = MutableLiveData()

    val habit: LiveData<Habit?> = repository.getHabit(habitId)

    init {
        loadHabit()
    }

    private fun loadHabit() {
        mutableHabit = MutableLiveData(repository.getHabit(habitId).value)
    }

    fun createOrUpdateHabit(newHabit: Habit) {
        repository.createOrUpdate(newHabit)
    }

    fun deleteHabit(newHabit: Habit) {
        repository.delete(newHabit)
    }
}