package ru.netology.diploma.repository.author_wall_repo

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.dto.Post

interface AuthorWallRepository {
    val data: Flow<PagingData<Post>>
    suspend fun getAll(id: Int)
    suspend fun saveAuthorId(id: Int)
//    suspend fun clearDb()
}