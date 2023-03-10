package ru.netology.diploma.repository.author_wall_repo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface AuthorWallRepositoryModule {

    @Singleton
    @Binds
    fun bindsAuthorWallRepository(impl: AuthorWallRepositoryImpl): AuthorWallRepository
}