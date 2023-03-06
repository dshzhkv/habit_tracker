package com.example.habittracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class HabitAdapter(private val habits: List<Habit>,
                   private val context: Context,
                   private val onHabitCardListener: OnHabitCardListener)
    : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View, private val onHabitCardListener: OnHabitCardListener) : RecyclerView.ViewHolder(itemView), OnClickListener {
        val title: TextView = itemView.findViewById(R.id.title)
        val type: TextView = itemView.findViewById(R.id.type_tag)
        val priority: TextView = itemView.findViewById(R.id.priority_tag)
        val repeatInformation: TextView = itemView.findViewById(R.id.repeat_information)
        val description: TextView = itemView.findViewById(R.id.description)
        val colorTag: View = itemView.findViewById(R.id.habit_color_tag)
        val descriptionSection: LinearLayout = itemView.findViewById(R.id.description_section)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            onHabitCardListener.onHabitCardClick(adapterPosition)
        }
    }

    interface OnHabitCardListener{
        fun onHabitCardClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.habit_card, parent,
            false
        )
        return HabitViewHolder(itemView, onHabitCardListener)
    }

    override fun getItemCount(): Int {
        return habits.size
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.title.text = habit.title
        bindType(holder, habit.type)
        bindPriority(holder, habit.priority)
        holder.repeatInformation.text = getRepetitionInformation(habit.repetitionTimes, habit.repetitionPeriod)
        bindDescription(holder, habit.description)
        holder.colorTag.backgroundTintList = ContextCompat.getColorStateList(context, habit.colorId)
    }

    private fun bindType(holder: HabitViewHolder, habitType: HabitType) {
        holder.type.backgroundTintList = ContextCompat.getColorStateList(context, habitType.colorId)
        holder.type.text = context.getString(habitType.textId)
        holder.type.setTextColor(context.getColor(habitType.fontColorId))
    }

    private fun bindPriority(holder: HabitViewHolder, habitPriority: HabitPriority) {
        holder.priority.backgroundTintList = ContextCompat.getColorStateList(context, habitPriority.colorId)
        holder.priority.text = context.getString(habitPriority.textId)
        holder.priority.setTextColor(context.getColor(habitPriority.fontColorId))
    }

    private fun bindDescription(holder: HabitViewHolder, habitDescription: String?) {
        when (habitDescription.isNullOrEmpty()) {
            true -> {
                holder.descriptionSection.visibility = View.GONE
            }
            false -> holder.description.text = habitDescription
        }
    }

    private fun getRepetitionInformation(repetitionTimes: Int, repetitionPeriod: Period): String {
        if (repetitionTimes == 0) {
            return context.getString(R.string.never)
        }
        if (repetitionTimes == 1 && repetitionPeriod == Period.DAY) {
            return context.getString(R.string.every_day)
        }

        return "$repetitionTimes ${context.resources.getQuantityString(R.plurals.times, repetitionTimes)} ${context.getString(repetitionPeriod.textId)}"
    }
}
