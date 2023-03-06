package com.example.habittracker

import android.content.Intent
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
        if (habit != null) {
            titleEditText?.setText(habit.title)
            typeRadioGroup?.check(habit.type.radioButtonId)
            prioritySpinner?.setSelection(HabitPriority.values().indexOf(habit.priority))
            repetitionTimesEditText?.setText(habit.repetitionTimes.toString())
            repetitionPeriodSpinner?.setSelection(Period.values().indexOf(habit.repetitionPeriod))
            setSelectedColor(habit.colorId)
            descriptionEditText?.setText(habit.description)
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
        prioritySpinner?.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            HabitPriority.values().map { getString(it.textId) }
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
                val type = when (typeRadioGroup?.checkedRadioButtonId) {
                    R.id.bad_habit_button -> HabitType.BAD
                    else -> HabitType.GOOD
                }
                val priority: HabitPriority = HabitPriority.values()[prioritySpinner?.selectedItemPosition ?: 0]
                val repetitionTimes: Int = repetitionTimesEditText?.text.toString().toInt()
                val repetitionPeriod: Period = Period.values()[repetitionPeriodSpinner?.selectedItemPosition ?: 0]
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

                val saveIntent: Intent = Intent(this, MainActivity::class.java)
                    .apply { putExtra(getString(R.string.intent_extra_habit), newHabit) }

                startActivity(saveIntent)
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

