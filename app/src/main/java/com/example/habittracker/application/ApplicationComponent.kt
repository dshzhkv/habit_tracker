package com.example.habittracker.application

import com.example.habittracker.view.fragments.edithabit.EditHabitFragment
import com.example.habittracker.view.fragments.filterbottomsheet.FilterBottomSheetFragment
import com.example.habittracker.view.fragments.habitslist.HabitsListFragment
import com.example.habittracker.view.fragments.main.MainFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DaggerModule::class])
interface ApplicationComponent {
    fun inject(mainFragment: MainFragment)
    fun inject(habitsListFragment: HabitsListFragment)
    fun inject(filterBottomSheetFragment: FilterBottomSheetFragment)
    fun inject(editHabitFragment: EditHabitFragment)
}