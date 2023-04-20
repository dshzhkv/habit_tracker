package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.domain.entities.Habit

@Database(entities = [Habit::class], version = 1, exportSchema = true)
@TypeConverters(DatabaseConverter::class)
abstract class HabitDatabaseImpl : RoomDatabase() {
    abstract fun habitDao() : HabitDao

    companion object {

        private var INSTANCE: HabitDatabaseImpl? = null

        fun getDatabase(context: Context): HabitDatabaseImpl {
            return INSTANCE ?: run {
                val instance = Room.databaseBuilder(
                    context,
                    HabitDatabaseImpl::class.java, "habits"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }
}