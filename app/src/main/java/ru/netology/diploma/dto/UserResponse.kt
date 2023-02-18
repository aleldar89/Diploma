package ru.netology.diploma.dto

/** UserResponse приходящий с сервера */

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

