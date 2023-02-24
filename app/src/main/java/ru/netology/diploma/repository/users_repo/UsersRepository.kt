package ru.netology.diploma.repository.users_repo

import ru.netology.diploma.dto.UserResponse

interface UsersRepository {
    suspend fun getUsers(): List<UserResponse>
    suspend fun getUserById(id: Int): UserResponse
}