package com.example.data.service

import com.example.data.entities.DeleteHabitBody
import com.example.data.entities.HabitDoneBody
import com.example.domain.entities.HabitResponse
import com.example.data.entities.UidResponse
import com.example.domain.entities.HabitRequest
import retrofit2.http.*

interface HabitService {
    @GET("habit")
    suspend fun getAllHabits(): List<HabitResponse>

    @PUT("habit")
    suspend fun addOrUpdateHabit(@Body habit: HabitRequest): UidResponse

    @HTTP(method = "DELETE", path = "habit", hasBody = true)
    suspend fun deleteHabit(@Body uid: DeleteHabitBody)

    @POST("habit_done")
    suspend fun checkHabit(@Body habitDone: HabitDoneBody)
}