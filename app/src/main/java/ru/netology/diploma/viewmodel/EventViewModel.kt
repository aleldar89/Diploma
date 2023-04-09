package ru.netology.diploma.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.dto.*
import ru.netology.diploma.repository.event_repo.EventRepository
import ru.netology.diploma.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    authorJob = "",
    content = "",
    datetime = "",
    published = "",
    coords = Coordinates("", ""),
    type = Type.ONLINE,
    likeOwnerIds = emptyList(),
    likedByMe = false,
    speakerIds = emptyList(),
    participantsIds = emptyList(),
    participatedByMe = false,
    attachment = null,
    link = "",
    ownedByMe = false,
    users = emptyMap(),
)


@ExperimentalCoroutinesApi
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    val isAuthorized: Boolean
        get() = appAuth
            .data
            .value
            ?.token != null

    private val myId: Int?
        get() = appAuth
            .data
            .value
            ?.id

    private val _authorization = MutableLiveData(isAuthorized)
    val authorization: LiveData<Boolean>
        get() = _authorization

    private val _media: MutableLiveData<MediaAttachment?> = MutableLiveData(null)
    val media: LiveData<MediaAttachment?>
        get() = _media

    private val _error = SingleLiveEvent<Exception>()
    val error: LiveData<Exception>
        get() = _error

    private val edited = MutableLiveData(empty)

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    val data: Flow<PagingData<Event>> = appAuth.data
        .flatMapLatest { auth ->
            repository.data
                .map { events ->
                    events.map {
                        it.copy(ownedByMe = auth?.id == it.authorId)
                    }
                }
        }.flowOn(Dispatchers.Default)

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            try {
                repository.getAll()
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

    fun save() {
        edited.value?.let { post ->
            viewModelScope.launch {
                try {
                    when (media.value) {
                        is ImageAttachment -> media.value?.let {
                            it as ImageAttachment
                            repository.saveWithImage(post, it.file!!)
                        }
                        is AudioAttachment -> media.value?.uri?.let { uri ->
                            repository.saveWithAudio(post, uri)
                        }
                        is VideoAttachment -> media.value?.uri?.let { uri ->
                            repository.saveWithVideo(post, uri)
                        }
                        null -> repository.save(post)
                    }
                } catch (e: Exception) {
                    val last = repository.selectLast()
                    try {
                        repository.removeById(last.id)
                    } catch (e: Exception) {
                        repository.localRemoveById(last.id)
                    }
                    _error.value = e
                }
                _eventCreated.postValue(Unit)
            }
        }
        edited.value = empty
        clearMedia()
    }

    fun removeById(id: Int) {
        viewModelScope.launch {
            val old = repository.getById(id)
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                try {
                    repository.save(old)
                } catch (e: Exception) {
                    repository.localSave(old)
                }
                _error.value = e
            }
        }
    }

    fun likeById(event: Event) {
        viewModelScope.launch {
            val old = repository.getById(event.id)
            val newLikeOwnerIds = event.likeOwnerIds?.plus(myId!!)
            try {
                if (newLikeOwnerIds != null)
                    repository.likeById(event, newLikeOwnerIds)
            } catch (e: Exception) {
                try {
                    repository.save(old)
                } catch (e: Exception) {
                    repository.localSave(old)
                }
                _error.value = e
            }
        }
    }

    fun dislikeById(event: Event) {
        viewModelScope.launch {
            val old = repository.getById(event.id)
            val newLikeOwnerIds = event.likeOwnerIds?.filter { it != myId }
            try {
                if (newLikeOwnerIds != null)
                    repository.dislikeById(event, newLikeOwnerIds)
            } catch (e: Exception) {
                try {
                    repository.save(old)
                } catch (e: Exception) {
                    repository.localSave(old)
                }
                _error.value = e
            }
        }
    }

    fun participateById(event: Event) {
        viewModelScope.launch {
            val old = repository.getById(event.id)
            val newParticipantsIds = event.participantsIds?.plus(myId!!)
            try {
                if (newParticipantsIds != null)
                    repository.participateById(event, newParticipantsIds)
            } catch (e: Exception) {
                try {
                    repository.save(old)
                } catch (e: Exception) {
                    repository.localSave(old)
                }
                _error.value = e
            }
        }
    }

    fun unParticipateById(event: Event) {
        viewModelScope.launch {
            val old = repository.getById(event.id)
            val newParticipantsIds = event.participantsIds?.filter { it != myId }
            try {
                if (newParticipantsIds != null)
                    repository.unParticipateById(event, newParticipantsIds)
            } catch (e: Exception) {
                try {
                    repository.save(old)
                } catch (e: Exception) {
                    repository.localSave(old)
                }
                _error.value = e
            }
        }
    }

    fun edit(event: Event) {
        edited.value = event
    }

    fun changeContent(content: String, datetime: String, link: String?, coords: Coordinates?) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(
            content = text,
            datetime = datetime,
            link = link,
            coords = coords,
        )
    }

    fun clearEditedData() {
        edited.value = empty
    }

    fun attachImage(uri: Uri, file: File?) {
        clearMedia()
        _media.value = ImageAttachment(uri, file)
    }

    fun attachVideo(uri: Uri) {
        clearMedia()
        _media.value = VideoAttachment(uri)
    }

    fun attachAudio(uri: Uri) {
        clearMedia()
        _media.value = AudioAttachment(uri)
    }

    fun clearMedia() {
        _media.value = null
    }

}