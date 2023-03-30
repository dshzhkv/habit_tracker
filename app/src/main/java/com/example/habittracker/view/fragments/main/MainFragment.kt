package com.example.habittracker.view.fragments.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.habittracker.view.fragments.habitslist.HabitsListAdapter
import com.example.habittracker.R
import com.example.habittracker.entities.HabitType
import com.example.habittracker.view.fragments.filterbottomsheet.FilterBottomSheetFragment
import com.example.habittracker.viewmodel.HabitsListViewModel
import com.example.habittracker.viewmodel.HabitsListViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var viewModel: HabitsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(activity as ViewModelStoreOwner,
            HabitsListViewModelFactory())[HabitsListViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.type_habits_list)
        val habitsListAdapter = HabitsListAdapter(this)
        viewPager.adapter = habitsListAdapter

        setupTabLayout(view, viewPager)
        startFilterBottomSheet(savedInstanceState)
        setListenerOnAddHabitButton(view)

        viewModel.loadHabits()
    }

    private fun setupTabLayout(view: View, viewPager: ViewPager2) {
        val tabLayout: TabLayout = view.findViewById(R.id.type_tabs)

        TabLayoutMediator(tabLayout, viewPager) {tab, position ->
            val type: HabitType = HabitType.values()[position]
            tab.text = when (type) {
                HabitType.GOOD -> getString(R.string.good_habits_title)
                HabitType.BAD -> getString(R.string.bad_habits_title)
            }
        }.attach()
    }

    private fun startFilterBottomSheet(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            parentFragmentManager.beginTransaction().add(R.id.filter_bottom_sheet,
                FilterBottomSheetFragment()).commit()
        }
    }

    private fun setListenerOnAddHabitButton(view: View) {
        val addHabitButton: FloatingActionButton = view.findViewById(R.id.add_habit_button)
        addHabitButton.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToEditHabitFragment())
        }
    }
}