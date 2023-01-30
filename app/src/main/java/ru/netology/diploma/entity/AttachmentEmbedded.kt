package ru.netology.diploma.entity

import ru.netology.diploma.dto.Attachment
import ru.netology.diploma.dto.AttachmentType

data class AttachmentEmbedded(
    val url: String,
    val type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbedded(it.url, it.type)
        }
    }
}
