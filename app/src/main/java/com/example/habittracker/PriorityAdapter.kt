package com.example.habittracker

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.habittracker.entities.HabitPriority

class PriorityAdapter(
    private val context: Context,
    resource: Int,
    priorities: Array<HabitPriority>)
    : ArrayAdapter<HabitPriority>(context, resource, priorities) {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val itemView = layoutInflater.inflate(R.layout.priority_spinner_layout, null, true)
//        return setupView(itemView, position)
//    }

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = layoutInflater.inflate(R.layout.priority_spinner_layout, null, true)
        return setupView(itemView, position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (convertView == null) {
            val itemView = layoutInflater.inflate(R.layout.priority_spinner_layout, parent, false)
            return setupView(itemView, position)
        }
        return setupView(convertView, position)
    }

    private fun setupView(view: View, position: Int): View {
        val priority = getItem(position)
        val prioritySpinnerTag: TextView = view.findViewById(R.id.priority_spinner_tag)
        if (priority != null) {
            prioritySpinnerTag.backgroundTintList = ContextCompat.getColorStateList(context, priority.colorId)
            prioritySpinnerTag.text = context.getString(priority.textId)
            prioritySpinnerTag.setTextColor(context.getColor(priority.fontColorId))
        }

        return view
    }
}