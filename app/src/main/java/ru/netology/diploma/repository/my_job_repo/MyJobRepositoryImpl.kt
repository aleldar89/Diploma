package ru.netology.diploma.repository.my_job_repo

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.dao.JobDao
import ru.netology.diploma.dto.Job
import ru.netology.diploma.entity.JobEntity
import ru.netology.diploma.error.NetworkError
import ru.netology.diploma.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class MyJobRepositoryImpl @Inject constructor(
    private val jobDao: JobDao,
    private val apiService: ApiService,
) : MyJobRepository {

    override val data: Flow<List<Job>> = jobDao.getAll()
        .map { list->
            list.map { entity ->
                entity.toDto()
            }
        }.asFlow()

    override suspend fun getMyJobs() {
        try {
            val response = apiService.getMyJobs()
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            val jobs = response.body() ?: throw RuntimeException("body is null")
            jobDao.insert(jobs.map(JobEntity.Companion::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveMyJob(job: Job) {
        try {
            val jobEntity = JobEntity.fromDto(job)
            jobDao.insert(jobEntity)

            val response = apiService.saveMyJob(job)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            } else {
                val _job = response.body()
                if (_job != null) {
                    jobDao.updateId(_job.id)
                }
            }

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeByIdMyJob(id: Int) {
        try {
            jobDao.removeById(id)

            val response = apiService.removeByIdMyJob(id)
            if (!response.isSuccessful) {
                throw Exception(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun clearDb() {
        try {
            jobDao.clear()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getById(id: Int): Job {
        try {
            return jobDao.getById(id).toDto()
        } catch (e: Exception) {
            println(e.message)
            throw e
        }
    }

    override suspend fun localSave(job: Job) {
        try {
            val jobEntity = JobEntity.fromDto(job)
            jobDao.saveOld(jobEntity)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun localRemoveById(id: Int) {
        try {
            jobDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun selectLast(): Job {
        try {
            return jobDao.selectLast().toDto()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}