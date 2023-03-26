package com.example.habittracker.fragments.aboutapp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.habittracker.R
import com.example.habittracker.entities.HabitPriority
import com.example.habittracker.entities.HabitType

class AboutAppFragment : Fragment(R.layout.fragment_about_app) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHabitExample(view, R.id.good_habit_example, R.string.about_app_good_habit_title,
            HabitType.GOOD, HabitPriority.MEDIUM,
            R.string.about_app_good_habit_repeat_information,
            R.string.about_app_good_habit_description, R.color.gradient_color11)

        setupHabitExample(view, R.id.bad_habit_example, R.string.about_app_bad_habit_title,
            HabitType.BAD, HabitPriority.HIGH,
            R.string.about_app_bad_habit_repeat_information,
            R.string.about_app_bad_habit_description, R.color.gradient_color3)
    }

    private fun setupHabitExample(view: View, id: Int, titleTextId: Int, habitType: HabitType,
                                  habitPriority: HabitPriority, repeatInformationTextId: Int,
                                  descriptionTextId: Int, colorId: Int) {
        val habitExample: View = view.findViewById(id)

        val title: TextView = habitExample.findViewById(R.id.title)
        val type: TextView = habitExample.findViewById(R.id.type_tag)
        val priority: TextView = habitExample.findViewById(R.id.priority_tag)
        val repeatInformation: TextView = habitExample.findViewById(R.id.repeat_information)
        val description: TextView = habitExample.findViewById(R.id.description)
        val habitColorTag: View = habitExample.findViewById(R.id.habit_color_tag)

        val context = activity as Context

        title.text = getString(titleTextId)

        type.text = getString(habitType.textId)
        type.backgroundTintList = ContextCompat.getColorStateList(context, habitType.colorId)
        type.setTextColor(context.getColor(habitType.fontColorId))

        priority.backgroundTintList = ContextCompat.getColorStateList(context, habitPriority.colorId)
        priority.text = getString(habitPriority.textId)
        priority.setTextColor(context.getColor(habitPriority.fontColorId))

        repeatInformation.text = getString(repeatInformationTextId)
        description.text = getString(descriptionTextId)
        habitColorTag.backgroundTintList = ContextCompat.getColorStateList(context, colorId)
    }
}