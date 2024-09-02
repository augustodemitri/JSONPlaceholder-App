package com.example.jsonplaceholderapp.di

import android.content.Context
import com.example.jsonplaceholderapp.R
import com.example.jsonplaceholderapp.data.api.UserService
import com.example.jsonplaceholderapp.data.api.CommentsService
import com.example.jsonplaceholderapp.data.api.NewsApiService
import com.google.maps.android.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideRetrofit(context: Context): Retrofit =
        Retrofit.Builder()
            .baseUrl(context.resources.getString(R.string.BASE_URL))
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