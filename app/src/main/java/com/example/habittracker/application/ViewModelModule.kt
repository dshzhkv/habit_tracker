package com.example.habittracker.application

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.domain.interactors.CheckHabitInteractor
import com.example.domain.interactors.EditHabitInteractor
import com.example.domain.interactors.FilterHabitsInteractor
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
    fun provideEditHabitViewModel(editHabitInteractor: EditHabitInteractor) : EditHabitViewModel =
        ViewModelProvider(owner,
            EditHabitViewModelFactory(editHabitInteractor)
        )[EditHabitViewModel::class.java]

    @MainActivityScope
    @Provides
    fun provideHabitsListViewModel(filterHabitsInteractor: FilterHabitsInteractor,
                                   checkHabitInteractor: CheckHabitInteractor): HabitsListViewModel =
        ViewModelProvider(owner,
            HabitsListViewModelFactory(filterHabitsInteractor, checkHabitInteractor)
        )[HabitsListViewModel::class.java]
}