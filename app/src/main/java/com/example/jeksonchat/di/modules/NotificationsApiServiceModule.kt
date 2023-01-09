package com.example.jeksonchat.di.modules

import com.example.jeksonchat.datasource.NotificationsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationsApiServiceModule {

    @Provides
    @Singleton
    fun provideApiService(): NotificationsApiService {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create()

    }
}