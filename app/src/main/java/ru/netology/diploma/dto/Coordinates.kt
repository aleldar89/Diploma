package ru.netology.diploma.dto

import com.google.gson.annotations.SerializedName

data class Coordinates(
    val lat: String,
    @SerializedName("long")
    val longitude: String,
)