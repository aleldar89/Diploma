package ru.netology.diploma.repository.event_repo

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.dto.Event
import java.io.File

interface EventRepository {
    val data: Flow<PagingData<Event>>
    suspend fun getAll()
    suspend fun save(event: Event)
    suspend fun getById(id: Int): Event
    suspend fun removeById(id: Int)
    suspend fun likeById(event: Event)
    suspend fun dislikeById(event: Event)
    suspend fun addParticipantById(id: Int)
    suspend fun removeParticipantById(id: Int)

    suspend fun saveWithAttachment(event: Event, file: File)
    suspend fun localSave(event: Event)
    suspend fun localRemoveById(id: Int)
    suspend fun selectLast(): Event
}