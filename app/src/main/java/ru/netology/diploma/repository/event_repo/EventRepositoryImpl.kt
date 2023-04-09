package ru.netology.diploma.repository.event_repo

import android.content.Context
import android.database.SQLException
import android.net.Uri
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.dao.EventDao
import ru.netology.diploma.dao.EventRemoteKeyDao
import ru.netology.diploma.db.EventsDb
import ru.netology.diploma.dto.*
import ru.netology.diploma.entity.EventEntity
import ru.netology.diploma.entity.PostEntity
import ru.netology.diploma.error.*
import java.io.File
import java.io.IOException
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val apiService: ApiService,
    eventRemoteKeyDao: EventRemoteKeyDao,
    eventsDb: EventsDb,
    private val context: Context,
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { eventDao.getPagingSource() },
        remoteMediator = EventRemoteMediator(
            apiService = apiService,
            eventDao = eventDao,
            eventRemoteKeyDao = eventRemoteKeyDao,
            eventsDb = eventsDb
        )
    ).flow.map {
        it.map(EventEntity::toDto)
    }

    override suspend fun getAll() {
        try {
            val response = apiService.getAllEvents()
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            val events = response.body() ?: throw RuntimeException("body is null")

            eventDao.insert(events.map(EventEntity.Companion::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(event: Event) {
        try {
            val eventEntity = EventEntity.fromDto(event)
            eventDao.save(eventEntity)

            val response = apiService.saveEvent(event)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            } else {
                val _event = response.body()
                if (_event != null) {
                    eventDao.updateId(_event.id)
                }
            }

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithImage(event: Event, file: File) {
        try {
            val media = upload(file)

            val response = apiService.saveEvent(
                event.copy(
                    attachment = Attachment(
                        url = media.url,
                        type = AttachmentType.IMAGE
                    )
                )
            )

            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            } else {
                val _event = response.body()
                if (_event != null) {
                    eventDao.updateId(_event.id)
                }
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithVideo(event: Event, uri: Uri) {
        try {
            val media = upload(uri)

            val response = apiService.saveEvent(
                event.copy(
                    attachment = Attachment(
                        url = media.url,
                        type = AttachmentType.VIDEO
                    )
                )
            )

            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            } else {
                val _event = response.body()
                if (_event != null) {
                    eventDao.updateId(_event.id)
                }
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAudio(event: Event, uri: Uri) {
        try {
            val media = upload(uri)

            val response = apiService.saveEvent(
                event.copy(
                    attachment = Attachment(
                        url = media.url,
                        type = AttachmentType.AUDIO
                    )
                )
            )

            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            } else {
                val _event = response.body()
                if (_event != null) {
                    eventDao.updateId(_event.id)
                }
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getById(id: Int): Event {
        try {
            return eventDao.getById(id).toDto()
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Int) {
        try {
            eventDao.removeById(id)

            val response = apiService.removeByIdEvent(id)
            if (!response.isSuccessful) {
                throw Exception(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(event: Event, likeOwnerIds: List<Int>) {
        try {
            eventDao.likeById(event.id, likeOwnerIds)

            val response = apiService.likeByIdPost(event.id)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(event: Event, likeOwnerIds: List<Int>) {
        try {
            eventDao.likeById(event.id, likeOwnerIds)

            val response = apiService.dislikeByIdPost(event.id)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun participateById(event: Event, participantsIds: List<Int>) {
        try {
            eventDao.participateById(event.id, participantsIds)

            val response = apiService.addByIdParticipants(event.id)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun unParticipateById(event: Event, participantsIds: List<Int>) {
        try {
            eventDao.participateById(event.id, participantsIds)

            val response = apiService.removeByIdParticipants(event.id)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun localSave(event: Event) {
        try {
            val eventEntity = EventEntity.fromDto(event)
            eventDao.saveOld(eventEntity)
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun localRemoveById(id: Int) {
        try {
            eventDao.removeById(id)
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun selectLast(): Event {
        try {
            return eventDao.selectLast().toDto()
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun upload(file: File): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", file.name, file.asRequestBody()
            )
            val response = apiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun upload(uri: Uri): Media {
        try {
            context.contentResolver.openInputStream(uri).use { inputStream ->
                val media = MultipartBody.Part.createFormData(
                    "file",
                    "file",
                    withContext(Dispatchers.Default) {
                        requireNotNull(inputStream)
                            .readBytes()
                            .toRequestBody()
                    }
                )
                val response = apiService.upload(media)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                return response.body() ?: throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}