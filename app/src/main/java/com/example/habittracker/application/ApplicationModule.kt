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
import com.example.data.repository.HabitRepositoryImpl
import com.example.data.service.*
import com.example.domain.HabitRepository
import com.example.domain.interactors.CheckHabitInteractor
import com.example.domain.interactors.EditHabitInteractor
import com.example.domain.interactors.FilterHabitsInteractor

@Module
class ApplicationModule(private val context: Context) {
    @Provides
    fun provideContext(): Context = context

    @Singleton
    @Provides
    fun provideEditHabitInteractor(habitRepository: HabitRepository): EditHabitInteractor =
        EditHabitInteractor(habitRepository)

    @Singleton
    @Provides
    fun provideFilterHabitsInteractor(habitRepository: HabitRepository): FilterHabitsInteractor =
        FilterHabitsInteractor(habitRepository)

    @Singleton
    @Provides
    fun provideCheckHabitInteractor(habitRepository: HabitRepository): CheckHabitInteractor =
        CheckHabitInteractor(habitRepository)

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
            .registerTypeAdapter(Habit::class.java, HabitSerializer())
            .registerTypeAdapter(Habit::class.java, HabitDeserializer())
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