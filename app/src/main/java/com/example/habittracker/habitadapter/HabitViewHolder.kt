package com.example.habittracker.habitadapter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.entities.Habit
import com.example.habittracker.entities.HabitPriority
import com.example.habittracker.entities.HabitType
import com.example.habittracker.entities.Period
import com.example.habittracker.fragments.habitslist.OnHabitCardListener

class HabitViewHolder(itemView: View,
                      private val onHabitCardListener: OnHabitCardListener?,
                      private val context: Context)
    : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val title: TextView = itemView.findViewById(R.id.title)
    private val type: TextView = itemView.findViewById(R.id.type_tag)
    private val priority: TextView = itemView.findViewById(R.id.priority_tag)
    private val repeatInformation: TextView = itemView.findViewById(R.id.repeat_information)
    private val description: TextView = itemView.findViewById(R.id.description)
    private val colorTag: View = itemView.findViewById(R.id.habit_color_tag)
    private val descriptionSection: LinearLayout = itemView.findViewById(R.id.description_section)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        onHabitCardListener?.onHabitCardClick(adapterPosition)
    }

    fun bind(habit: Habit) {
        title.text = habit.title
        bindType(habit.type)
        bindPriority(habit.priority)
        repeatInformation.text = getRepetitionInformation(habit.repetitionTimes, habit.repetitionPeriod)
        bindDescription(habit.description)
        colorTag.backgroundTintList = ContextCompat.getColorStateList(context, habit.colorId)
    }

    private fun bindType(habitType: HabitType) {
        type.backgroundTintList = ContextCompat.getColorStateList(context, habitType.colorId)
        type.text = context.getString(habitType.textId)
        type.setTextColor(context.getColor(habitType.fontColorId))
    }

    private fun bindPriority(habitPriority: HabitPriority) {
        priority.backgroundTintList = ContextCompat.getColorStateList(context, habitPriority.colorId)
        priority.text = context.getString(habitPriority.textId)
        priority.setTextColor(context.getColor(habitPriority.fontColorId))
    }

    private fun bindDescription(habitDescription: String?) {
        when (habitDescription.isNullOrEmpty()) {
            true -> {
                descriptionSection.visibility = View.GONE
            }
            false -> description.text = habitDescription
        }
    }

    private fun getRepetitionInformation(repetitionTimes: Int, repetitionPeriod: Period): String {
        if (repetitionTimes == 0) {
            return context.getString(R.string.never)
        }
        if (repetitionTimes == 1 && repetitionPeriod == Period.DAY) {
            return context.getString(R.string.every_day)
        }

        return "$repetitionTimes ${context.resources.getQuantityString(R.plurals.times, repetitionTimes)} ${context.getString(repetitionPeriod.textId)}"
    }
}
