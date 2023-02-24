package ru.netology.diploma.auth

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val idKey = "ID_KEY"
    private val tokenKey = "TOKEN_KEY"

    private val _data = MutableStateFlow<Token?>(null)
    val data: StateFlow<Token?> = _data.asStateFlow()

    init {
        val id = prefs.getInt(idKey, 0).takeIf {
            prefs.contains(idKey)
        }
        val token = prefs.getString(tokenKey, null)

        if (id != null && token != null) {
            _data.value = Token(id, token)
        } else {
            prefs.edit { clear() }
        }
    }

    /** Требуется ли sendPushToken? */

    @Synchronized
    fun saveAuth(token: Token) {
        _data.value = token
        prefs.edit {
            putInt(idKey, token.id)
            putString(tokenKey, token.token)
        }
    }

    @Synchronized
    fun clearAuth() {
        _data.value = null
        prefs.edit { clear() }
    }

}