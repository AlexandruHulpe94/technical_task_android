package com.example.app

import com.example.app.data.network.UserApiService
import com.example.app.data.network.UsersApi
import com.example.app.repository.UserRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideApi(): UsersApi = UserApiService.getClient()

    @Provides
    fun provideUserRepository() = UserRepository()
}