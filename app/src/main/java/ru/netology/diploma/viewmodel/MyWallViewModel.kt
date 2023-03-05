package ru.netology.diploma.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.dto.Post
import ru.netology.diploma.dto.UserResponse
import ru.netology.diploma.repository.my_wall_repo.MyWallRepository
import ru.netology.diploma.util.SingleLiveEvent
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MyWallViewModel @Inject constructor(
    private val repository: MyWallRepository,
    private val appAuth: AppAuth,
    private val apiService: ApiService
) : ViewModel() {

    //TODO почему myId == null
    private val myId: Int
        get() = checkNotNull(appAuth.data.value?.id)

    init {
        getUser()
        loadPosts()
    }

    val data: Flow<PagingData<Post>> = repository.data.flowOn(Dispatchers.Default)

    private val _userResponse = MutableLiveData<UserResponse>(null)
    val userResponse: LiveData<UserResponse>
        get() = _userResponse

    private val _error = SingleLiveEvent<Exception>()
    val error: LiveData<Exception>
        get() = _error

    private fun getUser() {
        viewModelScope.launch {
            try {
                _userResponse.value = apiService.getByIdUser(myId).body()
            } catch (e: Exception) {
                _error.value = e
            }
        }
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

}