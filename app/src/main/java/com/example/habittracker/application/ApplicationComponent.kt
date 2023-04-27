package com.example.habittracker.application

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun viewModelComponent(viewModelModule: ViewModelModule): ViewModelComponent
}