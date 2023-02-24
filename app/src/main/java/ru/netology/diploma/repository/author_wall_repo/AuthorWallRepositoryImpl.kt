package ru.netology.diploma.repository.author_wall_repo

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.dao.PostDao
import ru.netology.diploma.dao.PostRemoteKeyDao
import ru.netology.diploma.db.PostsDb
import ru.netology.diploma.dto.Post
import ru.netology.diploma.entity.PostEntity
import ru.netology.diploma.error.*
import java.io.IOException
import javax.inject.Inject

class AuthorWallRepositoryImpl @Inject constructor(
    private val authorWallDao: PostDao,
    private val apiService: ApiService,
    authorWallRemoteKeyDao: PostRemoteKeyDao,
    authorWallDb: PostsDb
) : AuthorWallRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { authorWallDao.getPagingSource() },
        remoteMediator = AuthorWallRemoteMediator(
            apiService = apiService,
            authorWallDao = authorWallDao,
            authorWallRemoteKeyDao = authorWallRemoteKeyDao,
            authorWallDb = authorWallDb
        )
    ).flow.map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getAll(id: Int) {
        try {
            val response = apiService.getAuthorWall(id)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            val authorWall = response.body() ?: throw RuntimeException("body is null")

            authorWallDao.insert(authorWall.map(PostEntity.Companion::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}