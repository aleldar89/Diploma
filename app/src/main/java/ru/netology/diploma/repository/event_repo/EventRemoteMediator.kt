package ru.netology.diploma.repository.event_repo

import androidx.paging.*
import androidx.room.withTransaction
import okio.IOException
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.dao.EventDao
import ru.netology.diploma.dao.EventRemoteKeyDao
import ru.netology.diploma.db.EventsDb
import ru.netology.diploma.entity.EventEntity
import ru.netology.diploma.entity.EventRemoteKeyEntity
import ru.netology.diploma.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
    private val eventsDb: EventsDb
) : RemoteMediator<Int, EventEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    val id = eventRemoteKeyDao.max()
                    if (id != null)
                        apiService.getAfterEvents(id, state.config.pageSize)
                    else
                        apiService.getLatestEvents(state.config.pageSize)
                }
                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBeforeEvents(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            if (body.isEmpty()) {
                return MediatorResult.Success(true)
            }

            eventsDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        val id = eventRemoteKeyDao.max()
                        if (id != null) {
                            eventRemoteKeyDao.insert(
                                listOf(
                                    EventRemoteKeyEntity(
                                        EventRemoteKeyEntity.KeyType.AFTER,
                                        body.first().id
                                    ),
                                    EventRemoteKeyEntity(
                                        EventRemoteKeyEntity.KeyType.BEFORE,
                                        body.last().id
                                    )
                                )
                            )
                        } else {
                            eventRemoteKeyDao.insert(
                                EventRemoteKeyEntity(
                                    EventRemoteKeyEntity.KeyType.AFTER,
                                    body.first().id
                                )
                            )
                        }
                    }

                    LoadType.PREPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.KeyType.AFTER,
                                body.first().id
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.KeyType.BEFORE, body.last().id
                            )
                        )
                    }
                }

                eventDao.insert(body.map(EventEntity::fromDto))
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}