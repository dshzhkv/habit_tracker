package com.example.data.service

import com.example.data.entities.DeleteHabitBody
import com.example.data.entities.HabitDoneBody
import com.example.data.entities.UidResponse
import com.example.domain.entities.Habit
import retrofit2.http.*

interface HabitService {
    @GET("habit")
    suspend fun getAllHabits(): List<Habit>

    @PUT("habit")
    suspend fun addOrUpdateHabit(@Body habit: Habit): UidResponse

    @HTTP(method = "DELETE", path = "habit", hasBody = true)
    suspend fun deleteHabit(@Body uid: DeleteHabitBody)

    @POST("habit_done")
    suspend fun checkHabit(@Body habitDone: HabitDoneBody)
}