package com.example.habittracker.fragments.edithabit

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.TextViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.habittracker.*
import com.example.habittracker.fragments.colorpicker.ColorPickerDialogFragment
import com.example.habittracker.entities.Habit
import com.example.habittracker.entities.HabitPriority
import com.example.habittracker.entities.HabitType
import com.example.habittracker.entities.Period
import com.example.habittracker.extensions.customGetSerializable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

const val ARG_HABIT = "habit"
const val ARG_HABIT_POSITION = "habitPosition"

const val REQUEST_KEY = "setSelectedColorRequest"
const val BUNDLE_KEY = "selectedColorId"


class EditHabitFragment : Fragment() {

    private var habit: Habit? = null
    private var habitPosition: Int = -1

    private var title: TextInputEditText? = null
    private var titleLayout: TextInputLayout? = null
    private var type: RadioGroup? = null
    private var priority: Spinner? = null
    private var repetitionTimes: EditText? = null
    private var repetitionPeriod: Spinner? = null
    private var colorValue: TextView? = null
    private var selectedColorId: Int = R.color.default_color
    private var description: EditText? = null

    private lateinit var activityContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            habit = it.customGetSerializable(ARG_HABIT, Habit::class.java)
            habitPosition = it.getInt(ARG_HABIT_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_habit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityContext = activity as Context

        findViews(view)

        if (habit !== null) {
           autofill(habit!!)
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
        setSelectedColor(habit.colorId)
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
            R.layout.priority_spinner_layout,
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
            val colorPicker: DialogFragment = ColorPickerDialogFragment.newInstance(selectedColorId)
            parentFragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
                val selectedColorId = bundle.getInt(BUNDLE_KEY)
                setSelectedColor(selectedColorId)
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
                if (shouldEdit()) {
                    MainActivity.fakeHabits[habitPosition] = newHabit
                } else {
                    MainActivity.fakeHabits.add(newHabit)
                }
                findNavController().popBackStack()
            } else {
                titleLayout?.error = getString(R.string.edit_habit_title_required)
            }
        }
    }

    private fun isInputCorrect(): Boolean = title?.text.toString().isNotEmpty()

    private fun shouldEdit(): Boolean =
        habitPosition >= 0 && habitPosition < MainActivity.fakeHabits.size

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
            title,
            type,
            priority,
            repetitionTimes,
            repetitionPeriod,
            description,
            selectedColorId,
        )
    }

    private fun setSelectedColor(colorId: Int) {
        selectedColorId = colorId

        val colorValue = activityContext.getColor(colorId)
        if (this.colorValue != null) {
            TextViewCompat.setCompoundDrawableTintList(
                this.colorValue!!,
                ColorStateList.valueOf(colorValue)
            )
            this.colorValue!!.text = colorValue.toHex()
        }
    }
}