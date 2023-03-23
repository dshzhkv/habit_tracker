package com.example.habittracker.fragments.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.habittracker.fragments.habitslist.HabitsListAdapter
import com.example.habittracker.R
import com.example.habittracker.entities.HabitType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment(R.layout.fragment_main) {

    private var habitsListAdapter: HabitsListAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.type_habits_list)
        habitsListAdapter = HabitsListAdapter(this)
        viewPager.adapter = habitsListAdapter

        val tabLayout: TabLayout = view.findViewById(R.id.type_tabs)
        TabLayoutMediator(tabLayout, viewPager) {tab, position ->
            val type: HabitType = HabitType.values()[position]
            tab.text = when (type) {
                HabitType.GOOD -> getString(R.string.good_habits_title)
                HabitType.BAD -> getString(R.string.bad_habits_title)
            }
        }.attach()

        setListenerOnAddHabitButton(view)
    }

    private fun setListenerOnAddHabitButton(view: View) {
        val addHabitButton: FloatingActionButton = view.findViewById(R.id.add_habit_button)
        addHabitButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_editHabitFragment)
        }
    }
}