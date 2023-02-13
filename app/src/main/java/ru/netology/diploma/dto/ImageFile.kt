package ru.netology.diploma.dto

import android.net.Uri
import java.io.File

data class ImageFile(
    val uri: Uri? = null,
    val file: File? = null
)
