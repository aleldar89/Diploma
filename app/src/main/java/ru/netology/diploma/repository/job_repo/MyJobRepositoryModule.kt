package ru.netology.diploma.repository.job_repo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface MyJobRepositoryModule {

    @Singleton
    @Binds
    fun bindsJobRepository(impl: MyJobRepositoryImpl): MyJobRepository
}