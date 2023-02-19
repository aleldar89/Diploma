package ru.netology.diploma.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.dto.Post
import ru.netology.diploma.dto.UserResponse
import ru.netology.diploma.repository.author_wall_repo.AuthorWallRepository
import ru.netology.diploma.repository.my_wall_repo.MyWallRepository
import ru.netology.diploma.util.SingleLiveEvent
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class AuthorWallViewModel @Inject constructor(
    private val repository: AuthorWallRepository,
    private val appAuth: AppAuth,
    private val apiService: ApiService
) : ViewModel() {
}