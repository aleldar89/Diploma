package ru.netology.diploma.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.diploma.entity.JobEntity
import ru.netology.diploma.entity.PostEntity

@Dao
interface JobDao {
    @Query("SELECT * FROM JobEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<JobEntity>>

    @Query("SELECT * FROM JobEntity WHERE id = :id")
    suspend fun getById(id: Int): JobEntity

    @Query("DELETE FROM JobEntity WHERE id = :id")
    suspend fun removeById(id: Int)

    @Query("DELETE FROM JobEntity")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jobs: List<JobEntity>)

    suspend fun saveOld(job: JobEntity) = insert(job)

    @Query("UPDATE JobEntity SET id = :id WHERE id = 0")
    suspend fun updateId(id: Int)

    @Query("SELECT * FROM JobEntity ORDER BY ID DESC LIMIT 1")
    suspend fun selectLast(): JobEntity
}