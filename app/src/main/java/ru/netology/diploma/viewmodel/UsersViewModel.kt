package ru.netology.diploma.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.diploma.dto.UserPreview
import ru.netology.diploma.dto.UserResponse
import ru.netology.diploma.repository.users_repo.UsersRepository
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository
) : ViewModel() {

    private val _data = MutableLiveData(emptyList<UserResponse>())
    val data: LiveData<List<UserResponse>>
        get() = _data

    private val _users = MutableLiveData(emptyList<UserResponse>())
    val users: LiveData<List<UserResponse>>
        get() = _users

    private val _user = MutableLiveData<UserResponse>()
    val user: LiveData<UserResponse>
        get() = _user

    init {
        getUsers()
    }

    fun getUsersById(idList: List<Int>) {
        viewModelScope.launch {
            val result = mutableListOf<UserResponse>()
            idList.forEach { id ->
                result.add(data.value?.get(id)!!) //todo можно рациональнее
            }
            _users.value = result
        }
    }

    private fun getUsers() {
        viewModelScope.launch {
            try {
                _data.value = repository.getUsers()
            } catch (e: Exception) {
                println("Users loading error")
            }
        }
    }

    fun getUserById(id: Int) {
        viewModelScope.launch {
            try {
                _user.value = repository.getUserById(id)
            } catch (e: Exception) {
                println("Users loading error")
            }
        }
    }

}