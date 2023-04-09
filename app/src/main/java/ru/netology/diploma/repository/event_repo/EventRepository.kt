package ru.netology.diploma.repository.event_repo

import android.net.Uri
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.dto.Event
import java.io.File

interface EventRepository {
    val data: Flow<PagingData<Event>>
    suspend fun getAll()
    suspend fun save(event: Event)
    suspend fun saveWithImage(event: Event, file: File)
    suspend fun saveWithVideo(event: Event, uri: Uri)
    suspend fun saveWithAudio(event: Event, uri: Uri)
    suspend fun getById(id: Int): Event
    suspend fun removeById(id: Int)
    suspend fun likeById(event: Event, likeOwnerIds: List<Int>)
    suspend fun dislikeById(event: Event, likeOwnerIds: List<Int>)
    suspend fun participateById(event: Event, participantsIds: List<Int>)
    suspend fun unParticipateById(event: Event, participantsIds: List<Int>)

    suspend fun localSave(event: Event)
    suspend fun localRemoveById(id: Int)
    suspend fun selectLast(): Event
}