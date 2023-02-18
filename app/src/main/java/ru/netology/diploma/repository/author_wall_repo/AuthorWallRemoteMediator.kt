package ru.netology.diploma.repository.author_wall_repo

import androidx.paging.*
import androidx.room.withTransaction
import okio.IOException
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.dao.PostDao
import ru.netology.diploma.dao.PostRemoteKeyDao
import ru.netology.diploma.db.PostsDb
import ru.netology.diploma.entity.PostEntity
import ru.netology.diploma.entity.PostRemoteKeyEntity
import ru.netology.diploma.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class AuthorWallRemoteMediator(
    private val apiService: ApiService,
    private val authorWallDao: PostDao,
    private val authorWallRemoteKeyDao: PostRemoteKeyDao,
    private val authorWallDb: PostsDb
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        try {
            val response = when (loadType) {

                LoadType.REFRESH -> {
                    val id = authorWallRemoteKeyDao.max()
                    if (id != null)
                        apiService.getAfterMyWall(id, state.config.pageSize)
                    else
                        apiService.getLatestMyWall(state.config.pageSize)
                }

                LoadType.PREPEND -> return MediatorResult.Success(true)

                LoadType.APPEND -> {
                    val id = authorWallRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBeforeMyWall(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            if (body.isEmpty()) {
                return MediatorResult.Success(true)
            }

            authorWallDb.withTransaction {

                when (loadType) {
                    LoadType.REFRESH -> {
                        val id = authorWallRemoteKeyDao.max()
                        if (id != null) {
                            authorWallRemoteKeyDao.insert(
                                listOf(
                                    PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.AFTER, body.first().id),
                                    PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.BEFORE, body.last().id)
                                )
                            )
                        } else {
                            authorWallRemoteKeyDao.insert(
                                PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.AFTER, body.first().id)
                            )
                        }
                    }

                    LoadType.PREPEND -> {
                        authorWallRemoteKeyDao.insert(
                            PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.AFTER, body.first().id)
                        )
                    }

                    LoadType.APPEND -> {
                        authorWallRemoteKeyDao.insert(
                            PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.BEFORE, body.last().id
                            )
                        )
                    }
                }

                authorWallDao.insert(body.map(PostEntity::fromDto))
            }

            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }

}