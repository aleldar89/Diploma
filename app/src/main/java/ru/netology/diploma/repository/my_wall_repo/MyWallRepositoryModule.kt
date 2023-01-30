package ru.netology.diploma.repository.my_wall_repo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface MyWallRepositoryModule {

    @Singleton
    @Binds
    fun bindsMyWallRepository(impl: MyWallRepositoryImpl): MyWallRepository
}