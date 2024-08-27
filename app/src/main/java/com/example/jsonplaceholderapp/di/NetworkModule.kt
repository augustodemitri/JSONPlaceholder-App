package com.example.jsonplaceholderapp.di

import com.example.jsonplaceholderapp.data.api.UserService
import com.example.jsonplaceholderapp.data.api.CommentsService
import com.example.jsonplaceholderapp.data.api.NewsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNewsService(
        retrofit: Retrofit
    ): NewsApiService = retrofit.create(NewsApiService::class.java)

    @Provides
    @Singleton
    fun provideUserService(
        retrofit: Retrofit
    ): UserService = retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideCommentsService(
        retrofit: Retrofit
    ): CommentsService = retrofit.create(CommentsService::class.java)
}