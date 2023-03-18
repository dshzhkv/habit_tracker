package com.example.habittracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.entities.Habit
import com.example.habittracker.fragments.edithabit.EditHabitFragment
import com.example.habittracker.fragments.edithabit.OnEditHabitFragmentListener
import com.example.habittracker.fragments.habitslist.OnAddHabitButtonListener
import com.example.habittracker.fragments.habitslist.OnHabitCardListener
import com.example.habittracker.fragments.main.MainFragment

class MainActivity : AppCompatActivity(), OnHabitCardListener, OnAddHabitButtonListener,
    OnEditHabitFragmentListener {

    companion object {
        var fakeHabits: MutableList<Habit> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.root_layout, MainFragment.newInstance())
                .commit()
        }
//        if (savedInstanceState == null) {
//            supportFragmentManager
//                .beginTransaction()
//                .add(R.id.root_layout, HabitsListFragment.newInstance())
//                .commit()
//        }
    }

    override fun onHabitCardClick(position: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_layout, EditHabitFragment.newInstance(fakeHabits[position], position), EditHabitFragment::class.java.name)
            .addToBackStack(null)
            .commit()

        supportActionBar?.hide()
    }

    override fun onAddHabitButtonClick() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_layout, EditHabitFragment.newInstance(), EditHabitFragment::class.java.name)
            .addToBackStack(null)
            .commit()

        supportActionBar?.hide()
    }

    override fun onCloseButtonClick() {
        val editHabitFragment = supportFragmentManager.findFragmentByTag(EditHabitFragment::class.java.name)

        if (editHabitFragment != null) {
            supportFragmentManager
                .beginTransaction()
                .remove(editHabitFragment)
                .add(R.id.root_layout, MainFragment.newInstance())
                .commit()

            supportActionBar?.show()
        }
    }

    override fun onSaveButtonClick() {
        val editHabitFragment = supportFragmentManager.findFragmentByTag(EditHabitFragment::class.java.name)

        if (editHabitFragment != null) {
            supportFragmentManager
                .beginTransaction()
                .remove(editHabitFragment)
                .add(R.id.root_layout, MainFragment.newInstance())
                .commit()

            supportActionBar?.show()
        }
    }
}