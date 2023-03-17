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
import com.example.habittracker.*
import com.example.habittracker.fragments.colorpicker.ColorPickerDialogFragment
import com.example.habittracker.entities.Habit
import com.example.habittracker.entities.HabitPriority
import com.example.habittracker.entities.HabitType
import com.example.habittracker.entities.Period
import com.example.habittracker.extensions.customGetSerializable

private const val ARG_HABIT = "habit"
private const val ARG_HABIT_POSITION = "habitPosition"

const val REQUEST_KEY = "setSelectedColorRequest"
const val BUNDLE_KEY = "selectedColorId"


class EditHabitFragment : Fragment() {

    private var habit: Habit? = null
    private var habitPosition: Int = -1

    private var titleEditText: EditText? = null
    private var typeRadioGroup: RadioGroup? = null
    private var prioritySpinner: Spinner? = null
    private var repetitionTimesEditText: EditText? = null
    private var repetitionPeriodSpinner: Spinner? = null
    private var colorValueTextView: TextView? = null
    private var selectedColorId: Int = R.color.default_color
    private var descriptionEditText: EditText? = null

    private var titleIsRequiredMessage: TextView? = null

    private lateinit var activityContext: Context

    private var listener: OnEditHabitFragmentListener? = null

    companion object {
        fun newInstance(habit: Habit, position: Int) =
            EditHabitFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_HABIT, habit)
                    putInt(ARG_HABIT_POSITION, position)
                }
            }

        fun newInstance() = EditHabitFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnEditHabitFragmentListener
    }

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
        titleEditText = view.findViewById(R.id.title_edit_text)
        typeRadioGroup = view.findViewById(R.id.type_radio_group)
        prioritySpinner = view.findViewById(R.id.priority_spinner)
        repetitionTimesEditText = view.findViewById(R.id.repetition_times_edit_text)
        repetitionPeriodSpinner = view.findViewById(R.id.repetition_period_spinner)
        colorValueTextView = view.findViewById(R.id.color_value)
        descriptionEditText = view.findViewById(R.id.description_edit_text)

        titleIsRequiredMessage = view.findViewById(R.id.title_is_required_message)
    }

    private fun autofill(habit: Habit) {
        titleEditText?.setText(habit.title)
        typeRadioGroup?.check(when (habit.type) {
            HabitType.GOOD -> R.id.good_habit_button
            HabitType.BAD -> R.id.bad_habit_button
        })
        prioritySpinner?.setSelection(HabitPriority.values().indexOf(habit.priority))
        repetitionTimesEditText?.setText(habit.repetitionTimes.toString())
        repetitionPeriodSpinner?.setSelection(Period.values().indexOf(habit.repetitionPeriod))
        setSelectedColor(habit.colorId)
        descriptionEditText?.setText(habit.description)
    }

    private fun setListenerOnTitleEditText() {
        titleEditText?.doOnTextChanged { _, _, _, _ ->
            titleIsRequiredMessage?.visibility = View.GONE
        }
    }

    private fun setListenerOnTypeRadioGroup(view: View) {
        val repeatText: TextView = view.findViewById(R.id.repeat_text)
        typeRadioGroup?.setOnCheckedChangeListener { _, checkedItemId ->
            when (checkedItemId) {
                R.id.bad_habit_button -> repeatText.setText(R.string.edit_habit_allowed_text)
                R.id.good_habit_button -> repeatText.setText(R.string.edit_habit_repeat_text)
            }
        }
    }

    private fun setAdapterForPrioritySpinner() {
        prioritySpinner?.adapter = PriorityAdapter(
            activityContext,
            R.layout.priority_spinner_layout,
            HabitPriority.values()
        )
    }

    private fun setListenerOnRepetitionTimesEditText(view: View) {
        val repeatTimes: TextView = view.findViewById(R.id.repeat_times)

        repetitionTimesEditText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && repetitionTimesEditText?.text.toString().isEmpty()) {
                repetitionTimesEditText?.setText(0.toString())
            }
        }

        repetitionTimesEditText?.doAfterTextChanged {
            if (it.toString().isNotEmpty()) {
                repeatTimes.text = resources.getQuantityString(R.plurals.times, it.toString().toInt())
            }
        }
    }

    private fun setAdapterForRepetitionPeriodSpinner() {
        repetitionPeriodSpinner?.adapter = ArrayAdapter(
            activityContext,
            android.R.layout.simple_spinner_item,
            Period.values().map { getString(it.textId) }
        )
    }

    private fun setListenerOnColorValue() {
        colorValueTextView?.setOnClickListener {
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
        closeButton.setOnClickListener { listener?.onCloseButtonClick() }
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
                listener?.onSaveButtonClick()
            } else {
                titleIsRequiredMessage?.visibility = View.VISIBLE
            }
        }
    }

    private fun isInputCorrect(): Boolean = titleEditText?.text.toString().isNotEmpty()

    private fun shouldEdit(): Boolean =
        habitPosition >= 0 && habitPosition < MainActivity.fakeHabits.size

    private fun parseInput(): Habit {
        val title: String = titleEditText?.text.toString()
        val type: HabitType = when (typeRadioGroup?.checkedRadioButtonId) {
            R.id.bad_habit_button -> HabitType.BAD
            else -> HabitType.GOOD
        }
        val priority: HabitPriority =  HabitPriority.values()[prioritySpinner?.selectedItemPosition ?: 0]
        val repetitionTimes: Int = repetitionTimesEditText?.text.toString().toInt()
        val repetitionPeriod: Period = Period.values()[repetitionPeriodSpinner?.selectedItemPosition ?: 0]
        val description: String = descriptionEditText?.text.toString()

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
        if (colorValueTextView != null) {
            TextViewCompat.setCompoundDrawableTintList(
                colorValueTextView!!,
                ColorStateList.valueOf(colorValue)
            )
            colorValueTextView!!.text = colorValue.toHex()
        }
    }
}