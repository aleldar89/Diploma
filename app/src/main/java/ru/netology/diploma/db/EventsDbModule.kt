package ru.netology.diploma.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.diploma.dao.EventDao
import ru.netology.diploma.dao.EventRemoteKeyDao
import ru.netology.diploma.dao.PostDao
import ru.netology.diploma.dao.PostRemoteKeyDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class EventsDbModule {

    @Singleton
    @Provides
    fun provideDb(
        context: Context
    ): EventsDb = Room.databaseBuilder(context, EventsDb::class.java, "events.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideEventDao(
        eventsDb: EventsDb
    ): EventDao = eventsDb.eventDao()

    @Provides
    fun provideEventRemoteKeyDao(
        eventsDb: EventsDb
    ): EventRemoteKeyDao = eventsDb.eventRemoteKeyDao()
}