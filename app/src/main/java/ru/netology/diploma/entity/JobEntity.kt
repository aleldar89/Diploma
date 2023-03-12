package ru.netology.diploma.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.diploma.dto.Job

@Entity
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val position: String,
    val start: String,
    val finish: String? = null,
    val link: String? = null,
) {
    fun toDto() = Job(
        id = id,
        name = name,
        position = position,
        start = start,
        finish = finish,
        link = link,
    )

    companion object {
        fun fromDto(dto: Job) =
            JobEntity(
                id = dto.id,
                name = dto.name,
                position = dto.position,
                start = dto.start,
                finish = dto.finish,
                link = dto.link,
            )
    }
}