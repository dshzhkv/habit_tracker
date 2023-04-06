package com.example.habittracker.view.fragments.aboutapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentAboutAppBinding
import com.example.habittracker.databinding.HabitCardBinding
import com.example.habittracker.entities.HabitPriority
import com.example.habittracker.entities.HabitType

class AboutAppFragment : Fragment() {

    private lateinit var binding: FragmentAboutAppBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutAppBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHabitExample(binding.goodHabitExample, R.string.about_app_good_habit_title,
            HabitType.GOOD, HabitPriority.MEDIUM,
            R.string.about_app_good_habit_repeat_information,
            R.string.about_app_good_habit_description, R.color.gradient_color11)

        setupHabitExample(binding.badHabitExample, R.string.about_app_bad_habit_title,
            HabitType.BAD, HabitPriority.HIGH,
            R.string.about_app_bad_habit_repeat_information,
            R.string.about_app_bad_habit_description, R.color.gradient_color3)
    }

    private fun setupHabitExample(habitCard: HabitCardBinding, titleTextId: Int, habitType: HabitType,
                                  habitPriority: HabitPriority, repeatInformationTextId: Int,
                                  descriptionTextId: Int, colorId: Int) {

        val context = activity as Context

        habitCard.title.text = getString(titleTextId)

        habitCard.typeTag.text = getString(habitType.textId)
        habitCard.typeTag.backgroundTintList = ContextCompat.getColorStateList(context, habitType.colorId)
        habitCard.typeTag.setTextColor(context.getColor(habitType.fontColorId))

        habitCard.priorityTag.backgroundTintList = ContextCompat.getColorStateList(context, habitPriority.colorId)
        habitCard.priorityTag.text = getString(habitPriority.textId)
        habitCard.priorityTag.setTextColor(context.getColor(habitPriority.fontColorId))

        habitCard.repeatInformation.text = getString(repeatInformationTextId)
        habitCard.description.text = getString(descriptionTextId)
        habitCard.habitColorTag.backgroundTintList = ContextCompat.getColorStateList(context, colorId)
    }
}