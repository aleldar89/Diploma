package ru.netology.diploma.dto

data class UserResponse(
    val id: Int,
    val login: String,
    val name: String,
    val avatar: String?,
)

data class UserPreview(
    val name: String,
    val avatar: String?,
)

