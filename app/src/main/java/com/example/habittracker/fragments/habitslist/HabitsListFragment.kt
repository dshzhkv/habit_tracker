package com.example.habittracker.fragments.habitslist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.MainActivity
import com.example.habittracker.R
import com.example.habittracker.entities.Habit
import com.example.habittracker.entities.HabitType
import com.example.habittracker.extensions.customGetSerializable
import com.example.habittracker.habitadapter.HabitAdapter

private const val ARG_TYPE = "type"

class HabitsListFragment : Fragment(R.layout.fragment_habits_list) {

    private var type: HabitType = HabitType.GOOD
    private var habits: List<Habit> = listOf()

    companion object {
        fun newInstance(type: HabitType) = HabitsListFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_TYPE, type)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            type = it.customGetSerializable(ARG_TYPE, HabitType::class.java) ?: HabitType.GOOD
            habits = MainActivity.fakeHabits[type] ?: listOf()
        }

        val noHabitsMessage: TextView = view.findViewById(R.id.no_habits_message)
        noHabitsMessage.text = when (type) {
            HabitType.GOOD -> getString(R.string.habits_list_no_good_habits)
            HabitType.BAD -> getString(R.string.habits_list_no_bad_habits)
        }
        if (habits.isEmpty()) {
            noHabitsMessage.visibility = View.VISIBLE
        } else {
            noHabitsMessage.visibility = View.GONE
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.habits_list)
        val navController: NavController = findNavController()
        val habitAdapter = HabitAdapter(habits, type, activity as Context, navController)
        recyclerView.adapter = habitAdapter
    }
}