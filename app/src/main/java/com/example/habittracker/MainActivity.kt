package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), HabitAdapter.OnHabitCardListener {

    private var fakeHabits: MutableList<Habit> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fakeHabits = mutableListOf(
            Habit(
                title = "Бросить курить",
                type = HabitType.BAD,
                priority = HabitPriority.HIGH,
                repetitionTimes = 0,
                repetitionPeriod = Period.DAY,
                description = "Курить - здоровью вредить",
                colorId = R.color.gradient_color1
            ),
            Habit(
                title = "Заниматься спортом",
                type = HabitType.GOOD,
                priority = HabitPriority.HIGH,
                repetitionTimes = 3,
                repetitionPeriod = Period.WEEK,
                description = "ЗОЖ",
                colorId = R.color.gradient_color5
            ),
            Habit(
                title = "Правильно питаться",
                type = HabitType.GOOD,
                priority = HabitPriority.MEDIUM,
                repetitionTimes = 1,
                repetitionPeriod = Period.DAY,
                description = null,
                colorId = R.color.gradient_color11
            ),
            Habit(
                title = "Гулять на природе",
                type = HabitType.GOOD,
                priority = HabitPriority.LOW,
                repetitionTimes = 3,
                repetitionPeriod = Period.MONTH,
                description = null,
                colorId = R.color.gradient_color16
            ),
            Habit(
                title = "Читать книги",
                type = HabitType.GOOD,
                priority = HabitPriority.MEDIUM,
                repetitionTimes = 1,
                repetitionPeriod = Period.DAY,
                description = "Прочитать книгу 1, книгу 2 и книгу 3",
                colorId = R.color.gradient_color3
            ),
            Habit(
                title = "Не есть сладкое",
                type = HabitType.BAD,
                priority = HabitPriority.MEDIUM,
                repetitionTimes = 1,
                repetitionPeriod = Period.DAY,
                description = "Сладкая жизнь может быть и без шоколада",
                colorId= R.color.gradient_color1
            ),
        )

        setListenerOnAddHabitButton()
    }

    override fun onStart() {
        super.onStart()

        val newHabit: Habit? = intent.getSerializable(getString(R.string.intent_extra_habit), Habit::class.java)
        if (newHabit != null) {
            fakeHabits.add(newHabit)
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.habits_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HabitAdapter(fakeHabits, applicationContext, this)
    }

    private fun setListenerOnAddHabitButton() {
        val addHabitButton: FloatingActionButton = findViewById(R.id.add_habit_button)
        addHabitButton.setOnClickListener {
            val intent = Intent(this, EditHabitActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onHabitCardClick(position: Int) {
        val intent = Intent(this, EditHabitActivity::class.java)
            .apply { putExtra(getString(R.string.intent_extra_habit), fakeHabits[position]) }
        startActivity(intent)
    }
}