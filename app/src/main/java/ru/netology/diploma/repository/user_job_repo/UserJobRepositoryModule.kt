package ru.netology.diploma.repository.user_job_repo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface UserJobRepositoryModule {

    @Singleton
    @Binds
    fun bindsJobRepository(impl: UserJobRepositoryImpl): UserJobRepository
}