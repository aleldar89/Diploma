package ru.netology.diploma.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.dto.Job
import ru.netology.diploma.dto.UserResponse
import ru.netology.diploma.repository.my_job_repo.MyJobRepository
import ru.netology.diploma.repository.user_job_repo.UserJobRepository
import ru.netology.diploma.util.SingleLiveEvent
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class UserJobViewModel @Inject constructor(
    private val repository: UserJobRepository,
    private val apiService: ApiService,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val AUTHOR_ID = "AUTHOR_ID"
    }

    private val authorId: Int = checkNotNull(savedStateHandle[AUTHOR_ID])

    private val _userResponse = MutableLiveData<UserResponse>(null)
    val userResponse: LiveData<UserResponse>
        get() = _userResponse

    private val _error = SingleLiveEvent<Exception>()
    val error: LiveData<Exception>
        get() = _error

    val data = repository.data.asLiveData(Dispatchers.Default)

    init {
        clearJobs()
        getUser()
        loadJobs()
    }

    fun getUser() {
        viewModelScope.launch {
            try {
                _userResponse.value = apiService.getByIdUser(authorId).body()
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

    fun loadJobs() {
        viewModelScope.launch {
            try {
                repository.getByIdUserJobs(authorId)
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

    fun clearJobs() {
        viewModelScope.launch {
            try {
                repository.clearDb()
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

}