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
class MyWallDbModule {

    @Singleton
    @Provides
    fun provideDb(
        context: Context
    ): MyWallDb = Room.databaseBuilder(context, MyWallDb::class.java, "posts.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideMyWallDao(
        myWallDb: MyWallDb
    ): PostDao = myWallDb.myWallDao()

    @Provides
    fun provideMyWallRemoteKeyDao(
        myWallDb: MyWallDb
    ): PostRemoteKeyDao = myWallDb.myWallRemoteKeyDao()
}