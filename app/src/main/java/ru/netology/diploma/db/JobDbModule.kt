package ru.netology.diploma.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.diploma.dao.JobDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class JobDbModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): JobsDb = Room.databaseBuilder(context, JobsDb::class.java, "jobs.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideJobDao(
        jobsDb: JobsDb
    ): JobDao = jobsDb.jobDao()

}