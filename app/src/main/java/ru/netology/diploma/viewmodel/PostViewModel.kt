package ru.netology.diploma.viewmodel

import android.net.Uri
import androidx.lifecycle.*
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
import ru.netology.diploma.dto.Coordinates
import ru.netology.diploma.dto.ImageFile
import ru.netology.diploma.dto.Post
import ru.netology.diploma.repository.post_repo.PostRepository
import ru.netology.diploma.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    authorJob = "",
    content = "",
    published = "",
    coords = Coordinates("", ""),
    link = "",
    likeOwnerIds = emptyList(),
    mentionIds = emptyList(),
    mentionedMe = false,
    likedByMe = false,
    attachment = null,
    ownedByMe = false,
    users = emptyMap(),
)

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth,
    val state: SavedStateHandle,
) : ViewModel() {

    val isAuthorized: Boolean
        get() = appAuth
            .data
            .value
            ?.token != null

    private val _authorization = MutableLiveData(isAuthorized)
    val authorization: LiveData<Boolean>
        get() = _authorization

    private val noPhoto = ImageFile()
    private val _media = MutableLiveData(noPhoto)
    val media: LiveData<ImageFile>
        get() = _media

    private val _error = SingleLiveEvent<Exception>()
    val error: LiveData<Exception>
        get() = _error

    private val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    companion object {
        private const val AUTHOR_ID = "AUTHOR_ID"
    }

    fun saveCurrentAuthor(authorId: Int) {
        state[AUTHOR_ID] = authorId
    }

//    fun getCurrentAuthor(): Int {
//        return checkNotNull(savedStateHandle[AUTHOR_ID])
//    }

    val data: Flow<PagingData<Post>> = appAuth.data
        .flatMapLatest { auth ->
            repository.data
                .map { posts ->
                    posts.map {
                        it.copy(ownedByMe = auth?.id == it.authorId) //todo нет токена -> меню поста видимо
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
                _postCreated.postValue(Unit)
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

    fun likeById(post: Post) {
        viewModelScope.launch {
            val old = repository.getById(post.id)
            try {
                repository.likeById(post)
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

    fun dislikeById(post: Post) {
        viewModelScope.launch {
            val old = repository.getById(post.id)
            try {
                repository.dislikeById(post)
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

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(
            content = text,

            //todo захват координат и поле под link в newFragment
            link = null,
            coords = null,
        )
    }

    fun clearPhoto() {
        _media.value = null
    }

    fun changePhoto(uri: Uri, file: File) {
        _media.value = ImageFile(uri, file)
    }

}