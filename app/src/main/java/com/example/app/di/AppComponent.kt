package com.example.app.di

import com.example.app.AppModule
import com.example.app.repository.UserRepository
import com.example.app.view.MainActivity
import com.example.app.viewmodel.UserViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(userRepository: UserRepository)

    fun inject(viewModel : UserViewModel)

    fun inject(mainActivity: MainActivity)
}