package ru.netology.diploma.repository.my_wall_repo

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.dao.PostDao
import ru.netology.diploma.dao.PostRemoteKeyDao
import ru.netology.diploma.db.MyWallDb
import ru.netology.diploma.db.PostsDb
import ru.netology.diploma.dto.Attachment
import ru.netology.diploma.dto.AttachmentType
import ru.netology.diploma.dto.Media
import ru.netology.diploma.dto.Post
import ru.netology.diploma.entity.PostEntity
import ru.netology.diploma.error.*
import java.io.File
import java.io.IOException
import javax.inject.Inject

class MyWallRepositoryImpl @Inject constructor(
    private val myWallDao: PostDao,
    private val apiService: ApiService,
    myWallRemoteKeyDao: PostRemoteKeyDao,
    myWallDb: MyWallDb
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
        TODO("Not yet implemented")
    }

}