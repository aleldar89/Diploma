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
class PostsDbModule {

    @Singleton
    @Provides
    fun provideDb(
        context: Context
    ): PostsDb = Room.databaseBuilder(context, PostsDb::class.java, "posts.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePostDao(
        postsDb: PostsDb
    ): PostDao = postsDb.postDao()

    @Provides
    fun providePostRemoteKeyDao(
        postsDb: PostsDb
    ): PostRemoteKeyDao = postsDb.postRemoteKeyDao()
}