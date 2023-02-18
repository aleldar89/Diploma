package ru.netology.diploma.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.diploma.dao.EventDao
import ru.netology.diploma.dao.EventRemoteKeyDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class EventsDbModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
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