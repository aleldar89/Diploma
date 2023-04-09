package ru.netology.diploma.repository.post_repo

import android.net.Uri
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.dto.Coordinates
import ru.netology.diploma.dto.Post
import java.io.File

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun saveWithImage(post: Post, file: File)
    suspend fun saveWithVideo(post: Post, uri: Uri)
    suspend fun saveWithAudio(post: Post, uri: Uri)
    suspend fun getById(id: Int): Post
    suspend fun removeById(id: Int)
    suspend fun likeById(post: Post, likeOwnerIds: List<Int>)
    suspend fun dislikeById(post: Post, likeOwnerIds: List<Int>)

    suspend fun getAddress(coords: Coordinates): String?

    suspend fun localSave(post: Post)
    suspend fun localRemoveById(id: Int)
    suspend fun selectLast(): Post
}