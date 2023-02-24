package ru.netology.diploma.repository.users_repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.dto.UserResponse
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UsersRepository {

    override suspend fun getUsers(): List<UserResponse> {
        return withContext(Dispatchers.IO) {
            apiService.getUsers().let {
                it.body() ?: throw RuntimeException("body is null")
            }
        }
    }

    override suspend fun getUserById(id: Int): UserResponse {
        return withContext(Dispatchers.IO) {
            apiService.getByIdUser(id).let {
                it.body() ?: throw RuntimeException("body is null")
            }
        }
    }

}