package ru.netology.diploma.repository.my_wall_repo

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

class MyWallRepositoryImpl @Inject constructor(
    private val myWallDao: PostDao,
    private val apiService: ApiService,
    myWallRemoteKeyDao: PostRemoteKeyDao,
    myWallDb: PostsDb
) : MyWallRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { myWallDao.getPagingSource() },
        remoteMediator = MyWallRemoteMediator(
            apiService = apiService,
            myWallDao = myWallDao,
            myWallRemoteKeyDao = myWallRemoteKeyDao,
            myWallDb = myWallDb
        )
    ).flow.map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getAll() {
        try {
            val response = apiService.getMyWall()
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            val myWall = response.body() ?: throw RuntimeException("body is null")

            myWallDao.insert(myWall.map(PostEntity.Companion::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}