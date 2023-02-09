package ru.netology.diploma.viewmodel

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
    type = Type.OFFLINE,
    likeOwnerIds = emptyList(),
    likedByMe = false,
    speakerIds = emptyList(),
    participantsIds = emptyList(),
    participatedByMe = false,
    attachment = null,
    link = "",
    ownedByMe = false,
    users = emptyList(),
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

    private val _authorization = MutableLiveData(isAuthorized)
    val authorization: LiveData<Boolean>
        get() = _authorization

    private val noPhoto = AvatarFile()
    private val _media = MutableLiveData(noPhoto)
    val media: LiveData<AvatarFile>
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
                .map {events ->
                    events.map {
                        it.copy(ownedByMe = auth?.id == it.authorId)
                    }
                }
        }.flowOn(Dispatchers.Default)

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                repository.getAll()
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

    fun save() {
        edited.value?.let {
            viewModelScope.launch {
                try {
                    when (val mediaModel = _media.value) {
                        null -> repository.save(it)
                        else -> mediaModel.file?.let { file ->
                            repository.saveWithAttachment(it, file)
                        }
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
        clearPhoto()
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
            try {
                repository.likeById(event)
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
            try {
                repository.dislikeById(event)
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

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun clearPhoto() {
        _media.value = null
    }

    fun changePhoto(file: File) {
        _media.value = AvatarFile(file)
    }

}