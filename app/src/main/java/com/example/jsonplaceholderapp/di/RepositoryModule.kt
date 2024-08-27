package com.example.jsonplaceholderapp.di

import com.example.jsonplaceholderapp.data.repository.CommentsRepositoryImpl
import com.example.jsonplaceholderapp.data.repository.NewsRepositoryImpl
import com.example.jsonplaceholderapp.data.repository.UserRepositoryImpl
import com.example.jsonplaceholderapp.domain.repository.CommentsRepository
import com.example.jsonplaceholderapp.domain.repository.NewsRepository
import com.example.jsonplaceholderapp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    @Singleton
    abstract fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindsCommentsRepository(
        commentsRepositoryImpl: CommentsRepositoryImpl
    ): CommentsRepository
}