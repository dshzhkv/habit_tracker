package com.example.habittracker.view.fragments.filterbottomsheet

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.contains
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.habittracker.application.HabitTrackerApplication
import com.example.habittracker.R
import com.example.habittracker.databinding.FragmentFilterBottomSheetBinding
import com.example.domain.entities.FilterType
import com.example.domain.entities.HabitColor
import com.example.domain.entities.HabitPriority
import com.example.domain.entities.SortType
import com.example.domain.usecases.FilterHabitsUseCase
import com.example.habittracker.viewmodel.HabitsListViewModel
import com.example.habittracker.viewmodel.HabitsListViewModelFactory
import javax.inject.Inject


class MultiSelectOptions<T>(
    var values: Array<T>,
    var titles: Array<CharSequence>,
    var areChecked: BooleanArray
)

class FilterBottomSheetFragment : Fragment() {

    private lateinit var viewModel: HabitsListViewModel
    private lateinit var activityContext: Context
    private lateinit var binding: FragmentFilterBottomSheetBinding
    @Inject
    lateinit var filterHabitsUseCase: FilterHabitsUseCase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBottomSheetBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity?.application as HabitTrackerApplication).applicationComponent.inject(this)

        viewModel = ViewModelProvider(activity as ViewModelStoreOwner,
            HabitsListViewModelFactory(filterHabitsUseCase))[HabitsListViewModel::class.java]

        activityContext = activity as Context

        setupSearch(view)
        setupSort(view)

        setupFilter(view, FilterType.PRIORITY, R.id.filter_by_priority,
            MultiSelectOptions(
                HabitPriority.values(),
                HabitPriority.values().map { activityContext.getString(it.textId) }.toTypedArray(),
                viewModel.getCheckedPriorities()
            ),
            R.string.bottom_sheet_priority_title)

        setupFilter(view, FilterType.COLOR, R.id.filter_by_color,
            MultiSelectOptions(
                HabitColor.values(),
                HabitColor.values().map { activityContext.getString(it.nameId) }.toTypedArray(),
                viewModel.getCheckedColors()
            ),
            R.string.bottom_sheet_color_title)
    }

    private fun <T> setupFilter(view: View,
                                filterType: FilterType,
                                filterViewId: Int,
                                options: MultiSelectOptions<T>,
                                titleId: Int) {
        val filterView: LinearLayout = view.findViewById(filterViewId)
        setupMultiSelect(options, filterType, titleId, filterView)

        when (filterType) {
            FilterType.PRIORITY -> {
                val prioritiesViews: Map<HabitPriority, View> = mapOf(
                    HabitPriority.HIGH to binding.highPriority,
                    HabitPriority.MEDIUM to binding.mediumPriority,
                    HabitPriority.LOW to binding.lowPriority
                )

                viewModel.selectedPriorities.observe(viewLifecycleOwner) {selectedPriorities ->
                    run {
                        updatePriorityFilterPlaceholder(selectedPriorities, prioritiesViews, filterView)

                        options.areChecked = viewModel.getCheckedPriorities()
                        setupMultiSelect(options, FilterType.PRIORITY,
                            R.string.bottom_sheet_priority_title, filterView)
                    }
                }
            }
            FilterType.COLOR -> {
                val colorViews: List<View> = listOf(
                    binding.color1,
                    binding.color2,
                    binding.color3)
                val moreColorsNumber: TextView = binding.moreColorsNumber

                viewModel.selectedColors.observe(viewLifecycleOwner) {colors ->
                    run {
                        updateColorFilterPlaceholder(colors, moreColorsNumber, colorViews)
                        options.areChecked = viewModel.getCheckedColors()
                        setupMultiSelect(options, FilterType.COLOR,
                            R.string.bottom_sheet_color_title, filterView)
                    }
                }
            }
        }
    }

    private fun updatePriorityFilterPlaceholder(
        selectedPriorities:MutableSet<HabitPriority>,
        prioritiesViews: Map<HabitPriority, View>,
        filterByPriority: LinearLayout) {

        for (priority in HabitPriority.values()) {
            when (selectedPriorities.contains(priority)) {
                true -> {
                    val priorityView = prioritiesViews[priority]
                    priorityView?.let {
                        if (!filterByPriority.contains(priorityView)) {
                            filterByPriority.addView(priorityView)
                        }
                    }
                }
                false -> filterByPriority.removeView(prioritiesViews[priority])
            }
        }
    }

    private fun updateColorFilterPlaceholder(selectedColors: MutableSet<HabitColor>,
                                             moreColorsNumber: TextView, colorViews: List<View>) {
        val colorsList = selectedColors.toList()

        if (colorsList.size > 3) {
            moreColorsNumber.visibility = View.VISIBLE
            val text = "+${colorsList.size - 3}"
            moreColorsNumber.text = text

            for (i in 0..2) {
                colorViews[i].backgroundTintList = ColorStateList.valueOf(activityContext.getColor(colorsList[i].colorId))
                colorViews[i].visibility = View.VISIBLE
            }

        } else {
            moreColorsNumber.visibility = View.GONE

            for (i in colorsList.indices) {
                colorViews[i].backgroundTintList = ColorStateList.valueOf(activityContext.getColor(colorsList[i].colorId))
                colorViews[i].visibility = View.VISIBLE
            }
            for (i in colorsList.size..2) {
                colorViews[i].visibility = View.GONE
            }
        }
    }

    private fun <T> setupMultiSelect(options: MultiSelectOptions<T>, filterType: FilterType,
                                     titleId: Int, filterView: View) {
        val dialogBuilder = AlertDialog.Builder(activityContext)

        dialogBuilder.setMultiChoiceItems(options.titles, options.areChecked) { _, index, isChecked ->
            if (isChecked) {
                viewModel.addToFilter(options.values[index], filterType)
            } else {
                viewModel.removeFromFilter(options.values[index], filterType)
            }
        }
            .setTitle(titleId)

        filterView.setOnClickListener {
            dialogBuilder.show()
        }
    }

    private fun setupSearch(view: View) {
        val search:androidx.appcompat.widget.SearchView = view.findViewById(R.id.search)

        search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.search(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.search(newText)
                return false
            }
        })
    }

    private fun setupSort(view: View) {
        val sort: Spinner = view.findViewById(R.id.sort)
        sort.adapter = ArrayAdapter(
            activity as Context,
            android.R.layout.simple_spinner_item,
            SortType.values().map { getString(it.textId) }
        )
        sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.sortBy(SortType.values()[position])
            }
        }
    }
}