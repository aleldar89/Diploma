package ru.netology.diploma.repository.post_repo

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.dto.Post
import java.io.File

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, file: File)
    suspend fun getById(id: Int): Post
    suspend fun removeById(id: Int)
    suspend fun likeById(post: Post)
    suspend fun dislikeById(post: Post)

    suspend fun localSave(post: Post)
    suspend fun localRemoveById(id: Int)
    suspend fun selectLast(): Post
}