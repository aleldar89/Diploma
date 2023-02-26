package ru.netology.diploma.repository.job_repo

import androidx.lifecycle.LiveData
import ru.netology.diploma.dto.Job

interface JobRepository {
    suspend fun getMyJobs(): LiveData<List<Job>>
    suspend fun saveMyJob(job: Job)
    suspend fun removeByIdMyJob(id: Int)
    suspend fun getByIdUserJobs(id: Int)
}