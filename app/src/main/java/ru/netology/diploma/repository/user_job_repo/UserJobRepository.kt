package ru.netology.diploma.repository.user_job_repo

import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.dto.Job

interface UserJobRepository {
    val data: Flow<List<Job>>
    suspend fun getByIdUserJobs(id: Int)
    suspend fun clearDb()
}