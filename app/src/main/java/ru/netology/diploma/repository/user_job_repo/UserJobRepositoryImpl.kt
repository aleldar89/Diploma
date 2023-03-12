package ru.netology.diploma.repository.user_job_repo

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

class UserJobRepositoryImpl @Inject constructor(
    private val jobDao: JobDao,
    private val apiService: ApiService,
) : UserJobRepository {

    override val data: Flow<List<Job>> = jobDao.getAll()
        .map { list->
            list.map { entity ->
                entity.toDto()
            }
        }.asFlow()

    override suspend fun getByIdUserJobs(id: Int) {
        try {
            val response = apiService.getByIdUserJobs(id)
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

    override suspend fun clearDb() {
        try {
            jobDao.clear()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}