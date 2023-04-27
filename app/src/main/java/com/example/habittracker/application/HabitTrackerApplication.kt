package com.example.habittracker.application

import android.app.Application
import androidx.lifecycle.ViewModelStoreOwner


class HabitTrackerApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent
    lateinit var viewModelComponent: ViewModelComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    fun initViewModelComponent(owner: ViewModelStoreOwner) {
        viewModelComponent = applicationComponent.viewModelComponent(ViewModelModule(owner))
    }
}