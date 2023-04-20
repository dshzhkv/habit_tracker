package com.example.habittracker

import android.app.Application
import com.example.habittracker.entities.Habit
import com.example.habittracker.model.*
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Request


class HabitTrackerApplication : Application() {
    private val gson = GsonBuilder()
        .registerTypeAdapter(Habit::class.java, HabitJsonSerializer())
        .registerTypeAdapter(Habit::class.java, HabitJsonDeserializer())
        .create()

    private val client = OkHttpClient().newBuilder()
        .addInterceptor { chain ->
            val request: Request =
                chain.request().newBuilder().addHeader(
                    "Authorization",
                    BuildConfig.TOKEN).build()
            val response = chain.proceed(request)
            when (response.code()) {
                200 -> response
                else -> {
                    Thread.sleep(1000)
                    response.close()
                    chain.call().clone().execute()
                }
            }
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://droid-test-server.doubletapp.ru/api/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    private val service = retrofit.create(HabitService::class.java)
    private val database by lazy { HabitDatabase.getDatabase(this) }
    val repository by lazy { HabitRepository(database.habitDao(), service) }
}