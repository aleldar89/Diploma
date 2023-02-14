package ru.netology.diploma.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.diploma.dao.EventDao
import ru.netology.diploma.dao.EventRemoteKeyDao
import ru.netology.diploma.entity.Converters
import ru.netology.diploma.entity.EventEntity
import ru.netology.diploma.entity.EventRemoteKeyEntity

@Database(entities = [EventEntity::class, EventRemoteKeyEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class EventsDb: RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
}