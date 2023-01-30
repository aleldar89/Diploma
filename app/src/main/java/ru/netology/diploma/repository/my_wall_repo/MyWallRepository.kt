package ru.netology.diploma.repository.my_wall_repo

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.dto.Post

interface MyWallRepository {
    val data: Flow<PagingData<Post>>
    suspend fun getAll()
}