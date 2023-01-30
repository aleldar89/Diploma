package ru.netology.diploma.entity

import ru.netology.diploma.dto.Coordinates

data class CoordinatesEmbedded(
    val lat: String,
    val long: String,
) {
    fun toDto() = Coordinates(lat, long)

    companion object {
        fun fromDto(dto: Coordinates) = CoordinatesEmbedded(dto.lat, dto.long)
    }
}
