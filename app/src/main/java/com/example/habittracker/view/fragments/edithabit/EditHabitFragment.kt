package com.example.habittracker.view.fragments.edithabit

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.widget.TextViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.habittracker.*
import com.example.habittracker.entities.*
import com.example.habittracker.extensions.customGetSerializable
import com.example.habittracker.view.fragments.colorpicker.ColorPickerDialogFragment
import com.example.habittracker.view.MainActivity
import com.example.habittracker.viewmodel.EditHabitViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*


const val ARG_HABIT_Id = "habitId"

const val REQUEST_KEY = "setSelectedColorRequest"
const val BUNDLE_KEY = "selectedColor"


class EditHabitFragment : Fragment(R.layout.fragment_edit_habit) {
    private lateinit var viewModel: EditHabitViewModel

    private var habitId: Long = -1

    private var title: TextInputEditText? = null
    private var titleLayout: TextInputLayout? = null
    private var type: RadioGroup? = null
    private var priority: Spinner? = null
    private var repetitionTimes: EditText? = null
    private var repetitionPeriod: Spinner? = null
    private var colorValue: TextView? = null
    private var selectedColor: HabitColor = HabitColor.defaultColor()
    private var description: EditText? = null

    private lateinit var activityContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            habitId = it.getLong(ARG_HABIT_Id)
        }

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return EditHabitViewModel(MainActivity.model, habitId) as T
            }
        })[EditHabitViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityContext = activity as Context

        findViews(view)

        viewModel.habit.observe(viewLifecycleOwner) { habit ->
            if (habit != null) {
                autofill(habit)
            }
        }

        setListenerOnTitleEditText()
        setListenerOnTypeRadioGroup(view)
        setAdapterForPrioritySpinner()
        setListenerOnRepetitionTimesEditText(view)
        setAdapterForRepetitionPeriodSpinner()

        setListenerOnColorValue()

        setListenerOnCloseButton(view)
        setListenerOnSaveButton(view)
    }

    private fun findViews(view: View) {
        title = view.findViewById(R.id.habit_title)
        titleLayout = view.findViewById(R.id.habit_title_TextInputLayout)
        type = view.findViewById(R.id.type)
        priority = view.findViewById(R.id.priority)
        repetitionTimes = view.findViewById(R.id.repetition_times)
        repetitionPeriod = view.findViewById(R.id.repetition_period)
        colorValue = view.findViewById(R.id.color_value)
        description = view.findViewById(R.id.description_edit)
    }

    private fun autofill(habit: Habit) {
        title?.setText(habit.title)
        type?.check(when (habit.type) {
            HabitType.GOOD -> R.id.good_habit_button
            HabitType.BAD -> R.id.bad_habit_button
        })
        priority?.setSelection(HabitPriority.values().indexOf(habit.priority))
        repetitionTimes?.setText(habit.repetitionTimes.toString())
        repetitionPeriod?.setSelection(Period.values().indexOf(habit.repetitionPeriod))
        setSelectedColor(habit.color)
        description?.setText(habit.description)
    }

    private fun setListenerOnTitleEditText() {
        title?.doOnTextChanged { _, _, _, _ ->
            titleLayout?.error = null
        }
    }

    private fun setListenerOnTypeRadioGroup(view: View) {
        val repeatText: TextView = view.findViewById(R.id.repeat_text)
        type?.setOnCheckedChangeListener { _, checkedItemId ->
            when (checkedItemId) {
                R.id.bad_habit_button -> repeatText.setText(R.string.edit_habit_allowed_text)
                R.id.good_habit_button -> repeatText.setText(R.string.edit_habit_repeat_text)
            }
        }
    }

    private fun setAdapterForPrioritySpinner() {
        priority?.adapter = PriorityAdapter(
            activityContext,
            R.layout.priority_spinner_item_layout,
            HabitPriority.values()
        )
    }

    private fun setListenerOnRepetitionTimesEditText(view: View) {
        val repeatTimes: TextView = view.findViewById(R.id.repeat_times)

        repetitionTimes?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && repetitionTimes?.text.toString().isEmpty()) {
                repetitionTimes?.setText(0.toString())
            }
        }

        repetitionTimes?.doAfterTextChanged {
            if (it.toString().isNotEmpty()) {
                repeatTimes.text = resources.getQuantityString(R.plurals.times, it.toString().toInt())
            }
        }
    }

    private fun setAdapterForRepetitionPeriodSpinner() {
        repetitionPeriod?.adapter = ArrayAdapter(
            activityContext,
            android.R.layout.simple_spinner_item,
            Period.values().map { getString(it.textId) }
        )
    }

    private fun setListenerOnColorValue() {
        colorValue?.setOnClickListener {
            val colorPicker: DialogFragment = ColorPickerDialogFragment.newInstance(selectedColor)
            parentFragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
                val selectedColor = bundle.customGetSerializable(BUNDLE_KEY, HabitColor::class.java)
                    ?: HabitColor.defaultColor()
                setSelectedColor(selectedColor)
            }
            colorPicker.show(parentFragmentManager, ColorPickerDialogFragment::class.java.name)
        }
    }

    private fun setListenerOnCloseButton(view: View) {
        val closeButton: ImageButton = view.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setListenerOnSaveButton(view: View) {
        val saveButton: Button = view.findViewById(R.id.save_habit_button)

        saveButton.setOnClickListener {
            if (isInputCorrect()) {
                val newHabit = parseInput()
                viewModel.createOrUpdateHabit(habitId, newHabit)
                findNavController().popBackStack()
            } else {
                titleLayout?.error = getString(R.string.edit_habit_title_required)
            }
        }
    }

    private fun isInputCorrect(): Boolean = title?.text.toString().isNotEmpty()

    private fun parseInput(): Habit {
        val title: String = title?.text.toString()
        val type: HabitType = when (type?.checkedRadioButtonId) {
            R.id.bad_habit_button -> HabitType.BAD
            else -> HabitType.GOOD
        }
        val priority: HabitPriority =  HabitPriority.values()[priority?.selectedItemPosition ?: 0]
        val repetitionTimes: Int = repetitionTimes?.text.toString().toInt()
        val repetitionPeriod: Period = Period.values()[repetitionPeriod?.selectedItemPosition ?: 0]
        val description: String = description?.text.toString()

        return Habit(
            habitId,
            title,
            type,
            priority,
            repetitionTimes,
            repetitionPeriod,
            description,
            selectedColor,
            Date()
        )
    }

    private fun setSelectedColor(color: HabitColor) {
        selectedColor = color

        val colorValue = activityContext.getColor(color.colorId)
        if (this.colorValue != null) {
            TextViewCompat.setCompoundDrawableTintList(
                this.colorValue!!,
                ColorStateList.valueOf(colorValue)
            )
            this.colorValue!!.text = activityContext.getString(color.nameId)
        }
    }
}