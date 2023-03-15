package com.example.habittracker

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.example.habittracker.colorpicker.ColorPickerDialog
import com.example.habittracker.colorpicker.OnSaveColorListener
import com.example.habittracker.entities.Habit
import com.example.habittracker.entities.HabitPriority
import com.example.habittracker.entities.HabitType
import com.example.habittracker.entities.Period


class EditHabitActivity : AppCompatActivity(), OnSaveColorListener {

    private var titleEditText: EditText? = null
    private var typeRadioGroup: RadioGroup? = null
    private var prioritySpinner: Spinner? = null
    private var repetitionTimesEditText: EditText? = null
    private var repetitionPeriodSpinner: Spinner? = null
    private var colorValueTextView: TextView? = null
    private var selectedColorId: Int = R.color.default_color
    private var descriptionEditText: EditText? = null

    private var titleIsRequiredMessage: TextView? = null

    private var editedHabitPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_edit_habit)

        findViews()

        setListenerOnTitleEditText()
        setListenerOnTypeRadioGroup()
        setAdapterForPrioritySpinner()
        setListenerOnRepetitionTimesEditText()
        setAdapterForRepetitionPeriodSpinner()

        setListenerOnColorValue()

        setListenerOnCloseButton()
        setListenerOnSaveButton()
    }

    override fun onStart() {
        super.onStart()

        val habit: Habit? = intent.getSerializableExtra(getString(R.string.intent_extra_habit), Habit::class.java)
        val habitPosition: Int = intent.getIntExtra(getString(R.string.intent_extra_habit_position), -1)
        if (habit != null && habitPosition >= 0) {
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

            editedHabitPosition = habitPosition
        }
    }

    private fun findViews() {
        titleEditText = findViewById(R.id.title_edit_text)
        typeRadioGroup = findViewById(R.id.type_radio_group)
        prioritySpinner = findViewById(R.id.priority_spinner)
        repetitionTimesEditText = findViewById(R.id.repetition_times_edit_text)
        repetitionPeriodSpinner = findViewById(R.id.repetition_period_spinner)
        colorValueTextView = findViewById(R.id.color_value)
        descriptionEditText = findViewById(R.id.description_edit_text)

        titleIsRequiredMessage = findViewById(R.id.title_is_required_message)
    }

    private fun setListenerOnTitleEditText() {
        titleEditText?.doOnTextChanged { _, _, _, _ ->
            titleIsRequiredMessage?.visibility = View.GONE
        }
    }

    private fun setListenerOnTypeRadioGroup() {
        val repeatText: TextView = findViewById(R.id.repeat_text)
        typeRadioGroup?.setOnCheckedChangeListener { _, checkedItemId ->
            when (checkedItemId) {
                R.id.bad_habit_button -> repeatText.setText(R.string.edit_habit_allowed_text)
                R.id.good_habit_button -> repeatText.setText(R.string.edit_habit_repeat_text)
            }
        }
    }

    private fun setAdapterForPrioritySpinner() {
        prioritySpinner?.adapter = PriorityAdapter(
            this,
            R.layout.priority_spinner_layout,
            HabitPriority.values()
        )
    }

    private fun setListenerOnRepetitionTimesEditText() {
        val repeatTimes: TextView = findViewById(R.id.repeat_times)

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
            this,
            android.R.layout.simple_spinner_item,
            Period.values().map { getString(it.textId) }
        )
    }

    private fun setListenerOnColorValue() {
        colorValueTextView?.setOnClickListener {
            ColorPickerDialog(this, selectedColorId).show()
        }
    }

    private fun setListenerOnCloseButton() {
        val closeButton: ImageButton = findViewById(R.id.close_button)
        closeButton.setOnClickListener { finish() }
    }

    private fun setListenerOnSaveButton() {
        val saveButton: Button = findViewById(R.id.save_habit_button)

        saveButton.setOnClickListener {
            if (isInputCorrect()) {
                val newHabit = parseInput()
                if (shouldEdit()) {
                    MainActivity.fakeHabits[editedHabitPosition] = newHabit
                } else {
                    MainActivity.fakeHabits.add(newHabit)
                }
                finish()
            } else {
                titleIsRequiredMessage?.visibility = View.VISIBLE
            }
        }
    }

    private fun isInputCorrect(): Boolean = titleEditText?.text.toString().isNotEmpty()

    private fun shouldEdit(): Boolean =
        editedHabitPosition >= 0 && editedHabitPosition < MainActivity.fakeHabits.size

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

    override fun setSelectedColor(colorId: Int) {
        selectedColorId = colorId

        val colorValue = getColor(colorId)
        if (colorValueTextView != null) {
            TextViewCompat.setCompoundDrawableTintList(colorValueTextView!!,
                ColorStateList.valueOf(colorValue))
            colorValueTextView!!.text = colorValue.toHex()
        }
    }
}

