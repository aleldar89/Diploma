package ru.netology.diploma.repository.job_repo

import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.dto.Job

interface MyJobRepository {
    val data: Flow<List<Job>>
    suspend fun getMyJobs()
    suspend fun saveMyJob(job: Job)
    suspend fun removeByIdMyJob(id: Int)
    suspend fun getByIdUserJobs(id: Int): List<Job>

    suspend fun getById(id: Int): Job
    suspend fun localSave(job: Job)
    suspend fun localRemoveById(id: Int)
    suspend fun selectLast(): Job
}