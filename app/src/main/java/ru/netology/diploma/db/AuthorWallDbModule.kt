package ru.netology.diploma.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.diploma.dao.PostDao
import ru.netology.diploma.dao.PostRemoteKeyDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AuthorWallDbModule {

    @Singleton
    @Provides
    fun provideDb(
        context: Context
    ): AuthorWallDb = Room.databaseBuilder(context, AuthorWallDb::class.java, "posts.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideAuthorDao(
        authorWallDb: AuthorWallDb
    ): PostDao = authorWallDb.authorWallDao()

    @Provides
    fun provideAuthorRemoteKeyDao(
        authorWallDb: AuthorWallDb
    ): PostRemoteKeyDao = authorWallDb.authorWallRemoteKeyDao()
}