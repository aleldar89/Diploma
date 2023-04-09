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
import ru.netology.diploma.extensions.dateFormatter
import ru.netology.diploma.repository.my_job_repo.MyJobRepository
import ru.netology.diploma.util.SingleLiveEvent
import javax.inject.Inject

private val empty = Job(
    id = 0,
    name = "",
    position = "",
    start = "",
    finish = null,
    link = null,
)

@ExperimentalCoroutinesApi
@HiltViewModel
class MyJobViewModel @Inject constructor(
    private val repository: MyJobRepository,
    private val appAuth: AppAuth,
    private val apiService: ApiService,
) : ViewModel() {

    private val myId: Int
        get() = checkNotNull(appAuth.data.value?.id)

    private val _userResponse = MutableLiveData<UserResponse>(null)
    val userResponse: LiveData<UserResponse>
        get() = _userResponse

    private val _error = SingleLiveEvent<Exception>()
    val error: LiveData<Exception>
        get() = _error

    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated: LiveData<Unit>
        get() = _jobCreated

    init {
        clearJobs()
        getUser()
        loadJobs()
    }

    private fun getUser() {
        viewModelScope.launch {
            try {
                _userResponse.value = apiService.getByIdUser(myId).body()
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

    private val edited = MutableLiveData(empty)

    val data = repository.data.asLiveData(Dispatchers.Default)

    fun loadJobs() {
        viewModelScope.launch {
            try {
                repository.getMyJobs()
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

    fun save() {
        edited.value?.let {
            viewModelScope.launch {
                try {
                    repository.saveMyJob(it)
                } catch (e: Exception) {
                    val last = repository.selectLast()
                    try {
                        repository.removeByIdMyJob(last.id)
                    } catch (e: Exception) {
                        repository.localRemoveById(last.id)
                    }
                    _error.value = e
                }
                _jobCreated.postValue(Unit)
            }
        }
        edited.value = empty
    }

    fun removeById(id: Int) {
        viewModelScope.launch {
            val old = repository.getById(id)
            try {
                repository.removeByIdMyJob(id)
            } catch (e: Exception) {
                try {
                    repository.saveMyJob(old)
                } catch (e: Exception) {
                    repository.localSave(old)
                }
                _error.value = e
            }
        }
    }

    fun changeContent(
        name: String,
        position: String,
        start: String,
        finish: String?,
        link: String?
    ) {
        edited.value = edited.value?.copy(
            name = name,
            position = position,
            start = start.dateFormatter(),
            finish = if (finish.isNullOrBlank()) null else finish.dateFormatter(),
            link = if (link.isNullOrBlank()) null else link,
        )
    }

    fun edit(job: Job) {
        edited.value = job
    }

    fun clearEditedData() {
        edited.value = empty
    }

    private fun clearJobs() {
        viewModelScope.launch {
            try {
                repository.clearDb()
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

}