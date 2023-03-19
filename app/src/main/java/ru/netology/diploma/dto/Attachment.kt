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

data class AttachmentImage(
    val uri: Uri? = null,
    val file: File? = null
)

data class AttachmentMV(
    val uri: Uri? = null
)