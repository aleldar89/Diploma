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
import ru.netology.diploma.dto.*
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

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _addresses = MutableLiveData<MutableMap<Int, String>>()
    val addresses: LiveData<MutableMap<Int, String>>
        get() = _addresses

    val data: Flow<PagingData<Post>> = appAuth.data
        .flatMapLatest { auth ->
            repository.data
                .map { posts ->
                    posts.map {
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
                _postCreated.postValue(Unit)
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

    fun likeById(post: Post) {
        viewModelScope.launch {
            val old = repository.getById(post.id)
            val newLikeOwnerIds = post.likeOwnerIds?.plus(myId!!)
            try {
                if (newLikeOwnerIds != null)
                    repository.likeById(post, newLikeOwnerIds)
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
            val newLikeOwnerIds = post.likeOwnerIds?.filter { it != myId }
            try {
                if (newLikeOwnerIds != null)
                    repository.dislikeById(post, newLikeOwnerIds)
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

    fun changeContent(content: String, link: String?, coords: Coordinates?) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(
            content = text,
            link = link,
            coords = coords,
        )
    }

    fun getAddress() {
        viewModelScope.launch {
            try {
                data.map {
                    it.map { post ->
                        if (post.coords != null) {
                            _addresses.value?.put(
                                post.id,
                                repository.getAddress(post.coords).toString()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = e
            }
        }
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