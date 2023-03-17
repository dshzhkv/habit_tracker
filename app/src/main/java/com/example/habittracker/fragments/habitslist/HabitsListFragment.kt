package com.example.habittracker.fragments.habitslist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.MainActivity
import com.example.habittracker.R
import com.example.habittracker.entities.Habit
import com.example.habittracker.habitadapter.HabitAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HabitsListFragment : Fragment(R.layout.fragment_habits_list) {
    private var onHabitCardListener: OnHabitCardListener? = null
    private var onAddHabitButtonListener: OnAddHabitButtonListener? = null

    private var habits: MutableList<Habit> = mutableListOf()

    companion object {
        fun newInstance() = HabitsListFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onHabitCardListener = context as OnHabitCardListener
        onAddHabitButtonListener = context as OnAddHabitButtonListener
        habits = MainActivity.fakeHabits
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noHabitsMessage: TextView = view.findViewById(R.id.no_habits_message)
        if (habits.isEmpty()) {
            noHabitsMessage.visibility = View.VISIBLE
        } else {
            noHabitsMessage.visibility = View.GONE
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.habits_list)
        val habitAdapter = HabitAdapter(habits, activity as Context, onHabitCardListener)
        recyclerView.adapter = habitAdapter

        setListenerOnAddHabitButton(view)
    }

    private fun setListenerOnAddHabitButton(view: View) {
        val addHabitButton: FloatingActionButton = view.findViewById(R.id.add_habit_button)
        addHabitButton.setOnClickListener {
            onAddHabitButtonListener?.onAddHabitButtonClick()
        }
    }
}