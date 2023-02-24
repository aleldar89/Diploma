package ru.netology.diploma.repository.users_repo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface UsersRepositoryModule {

    @Singleton
    @Binds
    fun bindsUsersRepository(impl: UsersRepositoryImpl): UsersRepository
}