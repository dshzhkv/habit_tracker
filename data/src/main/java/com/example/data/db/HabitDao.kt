package com.example.data.db

import androidx.room.*
import com.example.domain.entities.Habit
import com.example.domain.entities.HabitColor
import com.example.domain.entities.HabitPriority
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Upsert
    suspend fun createOrUpdate(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Query("SELECT * FROM habit WHERE id LIKE :id")
    fun getHabit(id: String?): Flow<Habit?>

    @Query("SELECT * FROM habit")
    fun getAll(): Flow<List<Habit>>

    @Query("SELECT * FROM habit " +
            "WHERE priority IN (:selectedPriorities) " +
            "AND color IN (:selectedColors) " +
            "AND title LIKE '%' || :searchQuery || '%' " +
            "ORDER BY " +
            "CASE WHEN :isAsc = 1 THEN editDate END ASC," +
            "CASE WHEN :isAsc = 0 THEN editDate END DESC")
    fun getFilteredAndSortedByEditDate(
        selectedPriorities: Set<HabitPriority>,
        selectedColors: Set<HabitColor>,
        isAsc: Boolean,
        searchQuery: String): Flow<List<Habit>>

    @Query("SELECT * FROM habit " +
            "WHERE priority IN (:selectedPriorities) " +
            "AND color IN (:selectedColors) " +
            "AND title LIKE '%' || :searchQuery || '%' " +
            "ORDER BY " +
            "CASE WHEN :isAsc = 1 THEN priority END ASC," +
            "CASE WHEN :isAsc = 0 THEN priority END DESC")
    fun getFilteredAndSortedByPriority(
        selectedPriorities: Set<HabitPriority>,
        selectedColors: Set<HabitColor>,
        isAsc: Boolean,
        searchQuery: String): Flow<List<Habit>>

    @Insert
    fun insert(habits: List<Habit>)

    @Query("DELETE FROM habit")
    fun clear()
}