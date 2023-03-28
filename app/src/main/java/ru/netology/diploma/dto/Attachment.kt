package ru.netology.diploma.dto

import android.net.Uri
import java.io.File

data class Attachment(
    val url: String,
    val type: AttachmentType,
)

enum class AttachmentType {
    IMAGE,
    AUDIO,
    VIDEO,
}

sealed interface MediaAttachment {
    val uri: Uri?
}

data class ImageAttachment(
    override val uri: Uri? = null,
    val file: File? = null
) : MediaAttachment

data class VideoAttachment(
    override val uri: Uri? = null
) : MediaAttachment

data class AudioAttachment(
    override val uri: Uri? = null
) : MediaAttachment