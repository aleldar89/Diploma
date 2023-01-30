package ru.netology.diploma.repository.my_wall_repo

import androidx.paging.*
import androidx.room.withTransaction
import okio.IOException
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.dao.PostDao
import ru.netology.diploma.dao.PostRemoteKeyDao
import ru.netology.diploma.db.MyWallDb
import ru.netology.diploma.entity.PostEntity
import ru.netology.diploma.entity.PostRemoteKeyEntity
import ru.netology.diploma.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class MyWallRemoteMediator(
    private val apiService: ApiService,
    private val myWallDao: PostDao,
    private val myWallRemoteKeyDao: PostRemoteKeyDao,
    private val myWallDb: MyWallDb
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        try {
            val response = when (loadType) {

                LoadType.REFRESH -> {
                    val id = myWallRemoteKeyDao.max()
                    if (id != null)
                        apiService.getAfterMyWall(id, state.config.pageSize)
                    else
                        apiService.getLatestMyWall(state.config.pageSize)
                }

                LoadType.PREPEND -> return MediatorResult.Success(true)

                LoadType.APPEND -> {
                    val id = myWallRemoteKeyDao.min() ?: return MediatorResult.Success(false)
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

            myWallDb.withTransaction {

                when (loadType) {
                    LoadType.REFRESH -> {
                        val id = myWallRemoteKeyDao.max()
                        if (id != null) {
                            myWallRemoteKeyDao.insert(
                                listOf(
                                    PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.AFTER, body.first().id),
                                    PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.BEFORE, body.last().id)
                                )
                            )
                        } else {
                            myWallRemoteKeyDao.insert(
                                PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.AFTER, body.first().id)
                            )
                        }
                    }

                    LoadType.PREPEND -> {
                        myWallRemoteKeyDao.insert(
                            PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.AFTER, body.first().id)
                        )
                    }

                    LoadType.APPEND -> {
                        myWallRemoteKeyDao.insert(
                            PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.BEFORE, body.last().id
                            )
                        )
                    }
                }

                myWallDao.insert(body.map(PostEntity::fromDto))
            }

            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }

}