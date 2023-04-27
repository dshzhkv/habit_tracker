package com.example.habittracker.application

import android.content.Context
import com.example.habittracker.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.example.domain.entities.Habit
import com.example.data.db.HabitDatabaseImpl
import com.example.data.entities.HabitDoneBody
import com.example.data.service.HabitJsonDeserializer
import com.example.data.service.HabitJsonSerializer
import com.example.data.repository.HabitRepositoryImpl
import com.example.data.service.HabitDoneBodySerializer
import com.example.data.service.HabitService
import com.example.domain.HabitRepository
import com.example.domain.usecases.EditHabitUseCase
import com.example.domain.usecases.FilterHabitsUseCase

@Module
class ApplicationModule(private val context: Context) {
    @Provides
    fun provideContext(): Context = context

    @Singleton
    @Provides
    fun provideEditHabitUseCase(habitRepository: HabitRepository): EditHabitUseCase =
        EditHabitUseCase(habitRepository)

    @Singleton
    @Provides
    fun provideFilterHabitsUseCase(habitRepository: HabitRepository): FilterHabitsUseCase =
        FilterHabitsUseCase(habitRepository)

    @Singleton
    @Provides
    fun provideDatabase(): HabitDatabaseImpl =
        HabitDatabaseImpl.getDatabase(context)

    @Singleton
    @Provides
    fun provideHabitRepository(habitDatabase: HabitDatabaseImpl, habitService: HabitService): HabitRepository =
        HabitRepositoryImpl(habitDatabase.habitDao(), habitService)

    @Singleton
    @Provides
    fun provideHabitService(retrofit: Retrofit): HabitService =
        retrofit.create(HabitService::class.java)

    @Singleton
    @Provides
    fun provideGson(): Gson =
        GsonBuilder()
            .registerTypeAdapter(Habit::class.java, HabitJsonSerializer())
            .registerTypeAdapter(Habit::class.java, HabitJsonDeserializer())
            .registerTypeAdapter(HabitDoneBody::class.java, HabitDoneBodySerializer())
            .create()

    @Singleton
    @Provides
    fun provideClient(): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor { chain ->
                val request: Request =
                    chain.request().newBuilder().addHeader(
                        "Authorization",
                        BuildConfig.TOKEN
                    ).build()
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

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://droid-test-server.doubletapp.ru/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
}