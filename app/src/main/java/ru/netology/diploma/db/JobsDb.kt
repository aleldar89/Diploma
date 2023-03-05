package ru.netology.diploma.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.diploma.dao.JobDao
import ru.netology.diploma.entity.Converters
import ru.netology.diploma.entity.JobEntity

@Database(entities = [JobEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class JobsDb: RoomDatabase() {
    abstract fun jobDao(): JobDao
}