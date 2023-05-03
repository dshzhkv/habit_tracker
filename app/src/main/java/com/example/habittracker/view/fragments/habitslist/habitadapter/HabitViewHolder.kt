package com.example.habittracker.view.fragments.habitslist.habitadapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.entities.*
import com.example.habittracker.R
import com.example.habittracker.databinding.HabitCardBinding
import com.example.habittracker.view.fragments.main.MainFragmentDirections


class HabitViewHolder(private val binding: HabitCardBinding,
                      private val navController: NavController,
                      private val onCheckHabit: (habit: Habit) -> Unit)
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
            onCheckHabit(habit)
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
