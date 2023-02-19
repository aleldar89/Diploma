package ru.netology.diploma.dto

import com.google.gson.annotations.SerializedName

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

//data class UserPreview(
//    @SerializedName("name")
//    val name: String,
//    @SerializedName("avatar")
//    val avatar: String?,
//)

