package ru.netology.diploma.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.auth.Token
import ru.netology.diploma.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class AuthentificationViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val apiService: ApiService
) : ViewModel() {

    private val _responseAuthState = MutableLiveData<Token?>(null)
    val responseAuthState: LiveData<Token?>
        get() = _responseAuthState

    private val _error = SingleLiveEvent<Exception>()
    val error: LiveData<Exception>
        get() = _error

    fun updateUser(login: String, password: String) {
        viewModelScope.launch {
            try {
                _responseAuthState.value = apiService.authUser(login, password).body()
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

    fun saveToken(token: Token) {
        appAuth.saveAuth(token)
    }

}

