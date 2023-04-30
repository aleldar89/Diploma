package ru.netology.diploma.repository.post_repo

import android.content.Context
import android.database.SQLException
import android.net.Uri
import androidx.paging.*
import com.google.gson.Gson
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.ToponymObjectMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.dao.PostDao
import ru.netology.diploma.dao.PostRemoteKeyDao
import ru.netology.diploma.db.PostsDb
import ru.netology.diploma.dto.*
import ru.netology.diploma.entity.PostEntity
import ru.netology.diploma.error.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    postsDb: PostsDb,
    private val context: Context,
) : PostRepository {

    private val apiKey = ""
    private val baseUrl =
        "https://geocode-maps.yandex.ru/1.x/?apikey=${apiKey}&geocode="
    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { postDao.getPagingSource() },
        remoteMediator = PostRemoteMediator(
            apiService = apiService,
            postDao = postDao,
            postRemoteKeyDao = postRemoteKeyDao,
            postsDb = postsDb
        )
    ).flow.map {
        it.map(PostEntity::toDto)
    }

//    @OptIn(ExperimentalPagingApi::class)
//    override val data: Flow<PagingData<Post>> = Pager(
//        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
//        pagingSourceFactory = { postDao.getPagingSource() },
//        remoteMediator = PostRemoteMediator(
//            apiService = apiService,
//            postDao = postDao,
//            postRemoteKeyDao = postRemoteKeyDao,
//            postsDb = postsDb
//        )
//    ).flow.map { pagingData ->
//        pagingData.map { postEntity ->
//            postEntity.toDto().copy(
//                address = postEntity.coords?.let {
//                    getAddress(it)
//                }
//            )
//        }
//    }

    override suspend fun getAll() {
        try {
            val response = apiService.getAllPosts()
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            val posts = response.body() ?: throw RuntimeException("body is null")

            postDao.insert(posts.map(PostEntity.Companion::fromDto))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val postEntity = PostEntity.fromDto(post)
            postDao.save(postEntity)

            val response = apiService.savePost(post)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            } else {
                val _post = response.body()
                if (_post != null) {
                    postDao.updateId(_post.id)
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

    override suspend fun saveWithImage(post: Post, file: File) {
        try {
            val media = upload(file)

            val response = apiService.savePost(
                post.copy(
                    attachment = Attachment(
                        url = media.url,
                        type = AttachmentType.IMAGE
                    )
                )
            )

            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            } else {
                val _post = response.body()
                if (_post != null) {
                    postDao.updateId(_post.id)
                }
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithVideo(post: Post, uri: Uri) {
        try {
            val media = upload(uri)

            val response = apiService.savePost(
                post.copy(
                    attachment = Attachment(
                        url = media.url,
                        type = AttachmentType.VIDEO
                    )
                )
            )

            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            } else {
                val _post = response.body()
                if (_post != null) {
                    postDao.updateId(_post.id)
                }
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAudio(post: Post, uri: Uri) {
        try {
            val media = upload(uri)

            val response = apiService.savePost(
                post.copy(
                    attachment = Attachment(
                        url = media.url,
                        type = AttachmentType.AUDIO
                    )
                )
            )

            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            } else {
                val _post = response.body()
                if (_post != null) {
                    postDao.updateId(_post.id)
                }
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getById(id: Int): Post {
        try {
            return postDao.getById(id).toDto()
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Int) {
        try {
            postDao.removeById(id)

            val response = apiService.removeByIdPost(id)
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

    override suspend fun likeById(post: Post, likeOwnerIds: List<Int>) {
        try {
            postDao.likeById(post.id, likeOwnerIds)

            val response = apiService.likeByIdPost(post.id)
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

    override suspend fun dislikeById(post: Post, likeOwnerIds: List<Int>) {
        try {
            postDao.likeById(post.id, likeOwnerIds)

            val response = apiService.dislikeByIdPost(post.id)
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

    override suspend fun getAddress(coords: Coordinates): String? {
        try {
            val request: Request = Request.Builder()
                .url("${baseUrl}${coords.longitude},${coords.lat}&kind=locality&format=json&results=1")
                .build()

            return withContext(Dispatchers.IO) {
                client.newCall(request)
                    .execute()
                    .let {
                        it.body?.string() ?: throw RuntimeException("body is null")
                    }
                    .let {
                        gson.fromJson(it, GeoObjectCollection::class.java)
                    }
                    .let {
                        it?.children?.firstOrNull()?.obj
                            ?.metadataContainer
                            ?.getItem(ToponymObjectMetadata::class.java)
                            ?.address
                            ?.components
                            ?.firstOrNull {
                                it.kinds.contains(Address.Component.Kind.LOCALITY)
                            }
                            ?.name
                    }
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun localSave(post: Post) {
        try {
            val postEntity = PostEntity.fromDto(post)
            postDao.saveOld(postEntity)
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun localRemoveById(id: Int) {
        try {
            postDao.removeById(id)
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun selectLast(): Post {
        try {
            return postDao.selectLast().toDto()
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