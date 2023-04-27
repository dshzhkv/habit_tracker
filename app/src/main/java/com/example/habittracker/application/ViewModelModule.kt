package com.example.habittracker.application

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.domain.usecases.EditHabitUseCase
import com.example.domain.usecases.FilterHabitsUseCase
import com.example.habittracker.viewmodel.EditHabitViewModel
import com.example.habittracker.viewmodel.EditHabitViewModelFactory
import com.example.habittracker.viewmodel.HabitsListViewModel
import com.example.habittracker.viewmodel.HabitsListViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule(private val owner: ViewModelStoreOwner) {

    @MainActivityScope
    @Provides
    fun provideEditHabitViewModel(editHabitUseCase: EditHabitUseCase) : EditHabitViewModel =
        ViewModelProvider(owner,
            EditHabitViewModelFactory(editHabitUseCase)
        )[EditHabitViewModel::class.java]

    @MainActivityScope
    @Provides
    fun provideHabitsListViewModel(filterHabitsUseCase: FilterHabitsUseCase): HabitsListViewModel =
        ViewModelProvider(owner,
            HabitsListViewModelFactory(filterHabitsUseCase)
        )[HabitsListViewModel::class.java]
}