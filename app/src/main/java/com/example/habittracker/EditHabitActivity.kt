package com.example.habittracker

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged


class EditHabitActivity : AppCompatActivity(), ColorPickerDialog.OnSaveColorListener {

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

        val habit: Habit? = intent.getSerializable(getString(R.string.intent_extra_habit), Habit::class.java)
        val habitPosition: Int = intent.getIntExtra(getString(R.string.intent_extra_habit_position), -1)
        if (habit != null && habitPosition >= 0) {
            titleEditText?.setText(habit.title)
            typeRadioGroup?.check(habit.type.radioButtonId)
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
            HabitPriority.values())
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
            Period.strings(this)
        )
    }

    private fun setListenerOnColorValue() {
        colorValueTextView?.setOnClickListener { showColorPicker() }
    }

    private fun showColorPicker() {
        val dialog = ColorPickerDialog(this, selectedColorId)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun setListenerOnCloseButton() {
        val closeButton: ImageButton = findViewById(R.id.close_button)
        closeButton.setOnClickListener { finish() }
    }

    private fun setListenerOnSaveButton() {
        val saveButton: Button = findViewById(R.id.save_habit_button)

        saveButton.setOnClickListener {

            val title = titleEditText?.text.toString()

            if (title.isEmpty()) {
                titleIsRequiredMessage?.visibility = View.VISIBLE
            } else {
                val type = HabitType from typeRadioGroup?.checkedRadioButtonId
                val priority: HabitPriority = HabitPriority at prioritySpinner?.selectedItemPosition
                val repetitionTimes: Int = repetitionTimesEditText?.text.toString().toInt()
                val repetitionPeriod: Period = Period at repetitionPeriodSpinner?.selectedItemPosition
                val description: String = descriptionEditText?.text.toString()

                val newHabit = Habit(
                    title,
                    type,
                    priority,
                    repetitionTimes,
                    repetitionPeriod,
                    description,
                    selectedColorId,
                )

                if (editedHabitPosition >= 0 && editedHabitPosition < MainActivity.fakeHabits.size) {
                    MainActivity.fakeHabits[editedHabitPosition] = newHabit
                } else {
                    MainActivity.fakeHabits.add(newHabit)
                }
                finish()
            }
        }
    }

    override fun setSelectedColor(colorId: Int) {
        selectedColorId = colorId

        val colorValue = getColor(colorId)
        colorValueTextView?.compoundDrawableTintList = ColorStateList.valueOf(colorValue)
        colorValueTextView?.text = colorValue.toHex()
    }
}

