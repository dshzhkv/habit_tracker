package com.example.habittracker.view.fragments.habitslist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.application.HabitTrackerApplication
import com.example.habittracker.viewmodel.HabitsListViewModel
import com.example.habittracker.R
import com.example.domain.entities.HabitType
import com.example.habittracker.extensions.customGetSerializable
import com.example.habittracker.view.fragments.habitslist.habitadapter.HabitAdapter
import com.example.habittracker.viewmodel.EditHabitViewModel
import javax.inject.Inject

private const val ARG_TYPE = "type"

class HabitsListFragment : Fragment(R.layout.fragment_habits_list) {

    @Inject
    lateinit var habitsListViewModel: HabitsListViewModel
    @Inject
    lateinit var editHabitViewModel: EditHabitViewModel
    private lateinit var type: HabitType

    companion object {
        fun newInstance(type: HabitType) = HabitsListFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_TYPE, type)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            type = it.customGetSerializable(ARG_TYPE, HabitType::class.java) ?: HabitType.GOOD
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity?.application as HabitTrackerApplication).viewModelComponent.inject(this)

        val recyclerView: RecyclerView = view.findViewById(R.id.habits_list)
        val navController: NavController = findNavController()
        val habitAdapter = HabitAdapter(navController, editHabitViewModel)
        recyclerView.adapter = habitAdapter

        val noHabitsMessage: TextView = view.findViewById(R.id.no_habits_message)
        noHabitsMessage.text = when (type) {
            HabitType.GOOD -> getString(R.string.habits_list_no_good_habits)
            HabitType.BAD -> getString(R.string.habits_list_no_bad_habits)
        }

        habitsListViewModel.habits.observe(viewLifecycleOwner) { habits ->
            val habitsOfType = habits.filter { it.type === type }
            habitAdapter.submitList(habitsOfType)

            if (habitsOfType.isEmpty()) {
                noHabitsMessage.visibility = View.VISIBLE
            } else {
                noHabitsMessage.visibility = View.GONE
            }
        }
    }
}