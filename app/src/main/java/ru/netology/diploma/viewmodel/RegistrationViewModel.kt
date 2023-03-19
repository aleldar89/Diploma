package ru.netology.diploma.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.diploma.api.ApiService
import ru.netology.diploma.auth.AppAuth
import ru.netology.diploma.auth.Token
import ru.netology.diploma.dto.AttachmentImage
import ru.netology.diploma.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val apiService: ApiService
) : ViewModel() {

    private val noPhoto = AttachmentImage()
    private val _media = MutableLiveData(noPhoto)
    val media: LiveData<AttachmentImage>
        get() = _media

    fun changePhoto(uri: Uri, file: File) {
        _media.value = AttachmentImage(uri, file)
    }

    private val _responseAuthState = MutableLiveData<Token?>(null)
    val responseAuthState: LiveData<Token?>
        get() = _responseAuthState

    private val _error = SingleLiveEvent<Exception>()
    val error: LiveData<Exception>
        get() = _error

    fun saveToken(token: Token) {
        appAuth.saveAuth(token)
    }

    fun registerUser(login: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                _responseAuthState.value = apiService.registerUser(
                    login.toRequestBody("text/plain".toMediaType()),
                    password.toRequestBody("text/plain".toMediaType()),
                    name.toRequestBody("text/plain".toMediaType()),
                ).body()
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

    fun registerWithPhoto(login: String, password: String, name: String, file: File) {
        viewModelScope.launch {
            try {
                _responseAuthState.value = apiService.registerWithPhoto(
                    login.toRequestBody("text/plain".toMediaType()),
                    password.toRequestBody("text/plain".toMediaType()),
                    name.toRequestBody("text/plain".toMediaType()),
                    MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
                ).body()
            } catch (e: Exception) {
                _error.value = e
            }
        }
    }

}