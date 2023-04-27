package com.example.habittracker.application

import com.example.habittracker.view.fragments.edithabit.EditHabitFragment
import com.example.habittracker.view.fragments.filterbottomsheet.FilterBottomSheetFragment
import com.example.habittracker.view.fragments.habitslist.HabitsListFragment
import dagger.Subcomponent

@MainActivityScope
@Subcomponent(modules = [ViewModelModule::class])
interface ViewModelComponent {
    fun inject(habitsListFragment: HabitsListFragment)
    fun inject(filterBottomSheetFragment: FilterBottomSheetFragment)
    fun inject(editHabitFragment: EditHabitFragment)
}