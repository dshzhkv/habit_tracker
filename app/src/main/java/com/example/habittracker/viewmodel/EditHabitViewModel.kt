package com.example.habittracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittracker.entities.Habit
import com.example.habittracker.model.HabitsListModel


class EditHabitViewModel(private val model: HabitsListModel, private val habitId: Long?) : ViewModel() {

    private var mutableHabit: MutableLiveData<Habit?> = MutableLiveData()

    val habit: LiveData<Habit?> = mutableHabit

    init {
        loadHabit()
    }

    private fun loadHabit() {
        mutableHabit.postValue(model.getHabit(habitId))
    }

    fun createOrUpdateHabit(id: Long?, newHabit: Habit) {
        model.createOrUpdateHabit(id, newHabit)
    }
}