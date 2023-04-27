package com.example.habittracker.view.fragments.edithabit

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
import com.example.habittracker.databinding.FragmentEditHabitBinding
import com.example.habittracker.extensions.customGetSerializable
import com.example.domain.entities.*
import com.example.habittracker.application.HabitTrackerApplication
import com.example.habittracker.view.fragments.colorpicker.ColorPickerDialogFragment
import com.example.habittracker.viewmodel.EditHabitViewModel
import java.util.*
import javax.inject.Inject


const val ARG_HABIT_ID = "habitId"

const val REQUEST_KEY = "setSelectedColorRequest"
const val BUNDLE_KEY = "selectedColor"


class EditHabitFragment : Fragment() {

    @Inject
    lateinit var viewModel: EditHabitViewModel
    private lateinit var binding: FragmentEditHabitBinding
    private lateinit var activityContext: Context

    private var habitId: String? = null
    private var habitDoneDates: List<Date> = listOf()
    private var selectedColor: HabitColor = HabitColor.defaultColor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            habitId = it.getString(ARG_HABIT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditHabitBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity?.application as HabitTrackerApplication).viewModelComponent.inject(this)

        activityContext = activity as Context

        val habit = viewModel.getHabit(habitId)
        if (habit != null) {
            autofill(habit)

            habitDoneDates = habit.doneDates

            binding.deleteButton.visibility = View.VISIBLE
            binding.deleteButton.setOnClickListener {
                viewModel.deleteHabit(habit.id)
                findNavController().popBackStack()
            }
        } else {
            binding.deleteButton.visibility = View.GONE
        }


        setListenerOnTitleEditText()
        setListenerOnTypeRadioGroup()
        setAdapterForPrioritySpinner()
        setListenerOnRepetitionTimesEditText()
        setAdapterForRepetitionPeriodSpinner()

        setListenerOnColorValue()

        setListenerOnCloseButton()
        setListenerOnSaveButton()
    }

    private fun autofill(habit: Habit) {
        binding.habitTitle.setText(habit.title)
        binding.type.check(when (habit.type) {
            HabitType.GOOD -> R.id.good_habit_button
            HabitType.BAD -> R.id.bad_habit_button
        })
        binding.priority.setSelection(HabitPriority.values().indexOf(habit.priority))
        binding.repetitionTimes.setText(habit.repetitionTimes.toString())
        binding.repetitionPeriod.setSelection(Period.values().indexOf(habit.repetitionPeriod))
        setSelectedColor(habit.color)
        binding.descriptionEdit.setText(habit.description)
    }

    private fun setListenerOnTitleEditText() {
        binding.habitTitle.doOnTextChanged { _, _, _, _ ->
            binding.habitTitleTextInputLayout.error = null
        }
    }

    private fun setListenerOnTypeRadioGroup() {
        binding.type.setOnCheckedChangeListener { _, checkedItemId ->
            when (checkedItemId) {
                R.id.bad_habit_button -> binding.repeatText.setText(R.string.edit_habit_allowed_text)
                R.id.good_habit_button -> binding.repeatText.setText(R.string.edit_habit_repeat_text)
            }
        }
    }

    private fun setAdapterForPrioritySpinner() {
        binding.priority.adapter = PriorityAdapter(
            activityContext,
            R.layout.priority_spinner_item_layout,
            HabitPriority.values()
        )
    }

    private fun setListenerOnRepetitionTimesEditText() {
        binding.repetitionTimes.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.repetitionTimes.text.toString().isEmpty()) {
                binding.repetitionTimes.setText(0.toString())
            }
        }

        binding.repetitionTimes.doAfterTextChanged {
            if (it.toString().isNotEmpty()) {
                binding.repeatTimes.text = resources.getQuantityString(R.plurals.times, it.toString().toInt())
            }
        }
    }

    private fun setAdapterForRepetitionPeriodSpinner() {
        binding.repetitionPeriod.adapter = ArrayAdapter(
            activityContext,
            android.R.layout.simple_spinner_item,
            Period.values().map { getString(it.textId) }
        )
    }

    private fun setListenerOnColorValue() {
        binding.colorValue.setOnClickListener {
            val colorPicker: DialogFragment = ColorPickerDialogFragment.newInstance(selectedColor)
            parentFragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
                val selectedColor = bundle.customGetSerializable(BUNDLE_KEY, HabitColor::class.java)
                    ?: HabitColor.defaultColor()
                setSelectedColor(selectedColor)
            }
            colorPicker.show(parentFragmentManager, ColorPickerDialogFragment::class.java.name)
        }
    }

    private fun setListenerOnCloseButton() {
        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setListenerOnSaveButton() {
        binding.saveHabitButton.setOnClickListener {
            if (isInputCorrect()) {
                val newHabit = parseInput()
                viewModel.createOrUpdateHabit(newHabit)
                findNavController().popBackStack()
            } else {
                binding.habitTitleTextInputLayout.error = getString(R.string.edit_habit_title_required)
            }
        }
    }

    private fun isInputCorrect(): Boolean = binding.habitTitle.text.toString().isNotEmpty()

    private fun parseInput(): Habit {
        val title: String = binding.habitTitle.text.toString()
        val type: HabitType = when (binding.type.checkedRadioButtonId) {
            R.id.bad_habit_button -> HabitType.BAD
            else -> HabitType.GOOD
        }
        val priority: HabitPriority =  HabitPriority.values()[binding.priority.selectedItemPosition]
        val repetitionTimes: Int = binding.repetitionTimes.text.toString().toInt()
        val repetitionPeriod: Period = Period.values()[binding.repetitionPeriod.selectedItemPosition]
        val description: String = binding.descriptionEdit.text.toString()

        return Habit(
            title,
            type,
            priority,
            repetitionTimes,
            repetitionPeriod,
            description,
            selectedColor,
            Date(),
            habitDoneDates,
            0,
            habitId ?: ""
        )
    }

    private fun setSelectedColor(color: HabitColor) {
        selectedColor = color

        val colorValue = activityContext.getColor(color.colorId)

        TextViewCompat.setCompoundDrawableTintList(
            binding.colorValue,
            ColorStateList.valueOf(colorValue)
        )
        binding.colorValue.text = activityContext.getString(color.nameId)
    }
}