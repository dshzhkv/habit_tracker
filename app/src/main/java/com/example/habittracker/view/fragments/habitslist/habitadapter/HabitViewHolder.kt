package com.example.habittracker.view.fragments.habitslist.habitadapter

import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.databinding.HabitCardBinding
import com.example.domain.entities.Habit
import com.example.domain.entities.HabitPriority
import com.example.domain.entities.HabitType
import com.example.domain.entities.Period
import com.example.habittracker.view.fragments.main.MainFragmentDirections
import com.example.habittracker.viewmodel.EditHabitViewModel


class HabitViewHolder(private val binding: HabitCardBinding,
                      private val navController: NavController,
                      private val viewModel: EditHabitViewModel)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(habit: Habit) {
        binding.title.text = habit.title
        bindType(habit.type)
        bindPriority(habit.priority)
        binding.repeatInformation.text = getRepetitionInformation(habit.repetitionTimes, habit.repetitionPeriod)
        bindDescription(habit.description)
        binding.habitColorTag.backgroundTintList = ContextCompat.getColorStateList(itemView.context, habit.color.colorId)

        itemView.setOnClickListener {
            navController.navigate(MainFragmentDirections.actionMainFragmentToEditHabitFragment(habit.id))
        }

        setOnCheckListener(habit)
    }

    private fun setOnCheckListener(habit: Habit) {
        binding.checkHabitButton.setOnClickListener {
            viewModel.checkHabit(habit.id)
            val (isHabitDone, doneTimes) = viewModel.isHabitDone(habit)
            var message: String = when (isHabitDone) {
                true -> {
                    when (habit.type) {
                        HabitType.GOOD -> itemView.context.getString(R.string.good_habit_done)
                        HabitType.BAD -> itemView.context.getString(R.string.bad_habit_not_ok)
                    }
                }
                false -> {
                    when (habit.type) {
                        HabitType.GOOD -> itemView.context.getString(R.string.good_habit_not_done)
                        HabitType.BAD -> itemView.context.getString(R.string.bad_habit_ok)
                    }
                }
            }
            message += " ${doneTimes}/${habit.repetitionTimes}"
            Toast
                .makeText(itemView.context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun bindType(habitType: HabitType) {
        binding.typeTag.backgroundTintList = ContextCompat.getColorStateList(itemView.context, habitType.colorId)
        binding.typeTag.text = itemView.context.getString(habitType.textId)
        binding.typeTag.setTextColor(itemView.context.getColor(habitType.fontColorId))
    }

    private fun bindPriority(habitPriority: HabitPriority) {
        binding.priorityTag.backgroundTintList = ContextCompat.getColorStateList(itemView.context, habitPriority.colorId)
        binding.priorityTag.text = itemView.context.getString(habitPriority.textId)
        binding.priorityTag.setTextColor(itemView.context.getColor(habitPriority.fontColorId))
    }

    private fun bindDescription(habitDescription: String?) {
        when (habitDescription.isNullOrEmpty()) {
            true -> {
                binding.descriptionSection.visibility = View.GONE
            }
            false -> binding.description.text = habitDescription
        }
    }

    private fun getRepetitionInformation(repetitionTimes: Int, repetitionPeriod: Period): String {
        if (repetitionTimes == 0) {
            return itemView.context.getString(R.string.never)
        }
        if (repetitionTimes == 1 && repetitionPeriod == Period.DAY) {
            return itemView.context.getString(R.string.every_day)
        }

        return "$repetitionTimes ${itemView.context.resources.getQuantityString(R.plurals.times, repetitionTimes)} ${itemView.context.getString(repetitionPeriod.textId)}"
    }
}
