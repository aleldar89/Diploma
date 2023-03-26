package ru.netology.diploma.viewmodel

import android.os.Bundle
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.diploma.dto.UserPreview
import ru.netology.diploma.dto.UserResponse
import ru.netology.diploma.repository.users_repo.UsersRepository
import ru.netology.diploma.util.StringArg
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val ID_ARRAY = "ID_ARRAY"
    }

    private var ids: IntArray = checkNotNull(savedStateHandle[ID_ARRAY])

    private val _data = MutableLiveData(listOf<UserResponse>())
    val data: LiveData<List<UserResponse>>
        get() = _data

    private val _users = MutableLiveData(listOf<UserResponse>())
    val users: LiveData<List<UserResponse>>
        get() = _users

    init {
        getUsers()
    }

    private fun getUsers() {
        viewModelScope.launch {
            try {
                _data.value = repository.getUsers()
                getUsersById(ids)
            } catch (e: Exception) {
                println("Users loading error")
            }
        }
    }

    private fun getUsersById(ids: IntArray) {
        viewModelScope.launch {
            val result = mutableListOf<UserResponse>()
            ids.forEach { id ->
                data.value?.forEach {
                    if (id == it.id)
                        result.add(it)
                }
            }
            _users.value = result
        }
    }

}