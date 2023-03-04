package ru.netology.diploma.repository.author_wall_repo

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.paging.*
import androidx.room.withTransaction
import com.google.gson.Gson
import okio.IOException
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.dao.PostDao
import ru.netology.diploma.dao.PostRemoteKeyDao
import ru.netology.diploma.db.PostsDb
import ru.netology.diploma.entity.PostEntity
import ru.netology.diploma.entity.PostRemoteKeyEntity
import ru.netology.diploma.error.ApiError
import ru.netology.diploma.ui.post_fragments.PostsFeedFragment.Companion.textArg
import ru.netology.diploma.util.StringArg
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class AuthorWallRemoteMediator(
    private val apiService: ApiService,
    private val authorWallDao: PostDao,
    private val authorWallRemoteKeyDao: PostRemoteKeyDao,
    private val authorWallDb: PostsDb,
    private val authorId: Int,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        try {
            val response = when (loadType) {

                LoadType.REFRESH -> {
                    val id = authorWallRemoteKeyDao.max()
                    if (id != null)
                        apiService.getAfterAuthorWall(
                            authorId = authorId,
                            postId = id,
                            count = state.config.pageSize
                        )
                    else
                        apiService.getLatestAuthorWall(
                            authorId = authorId,
                            count = state.config.pageSize
                        )
                }

                LoadType.PREPEND -> return MediatorResult.Success(true)

                LoadType.APPEND -> {
                    val id = authorWallRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBeforeAuthorWall(
                        authorId = authorId,
                        postId = id,
                        count = state.config.pageSize
                    )
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