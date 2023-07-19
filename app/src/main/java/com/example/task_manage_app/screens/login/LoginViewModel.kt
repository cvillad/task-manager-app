package com.example.task_manage_app.screens.login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_manage_app.api.TodoClient
import com.example.task_manage_app.models.AuthBody
import com.example.task_manage_app.validations.ValidationEvent
import com.example.task_manage_app.validations.Validator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val hasEmailError: Boolean = false,
    val hasPasswordError: Boolean = false
)

sealed class LoginEvent {
    data class UpdateEmail(val email: String): LoginEvent()
    data class UpdatePassword(val password: String): LoginEvent()

    object ValidateFields: LoginEvent()
}

class LoginViewModel: ViewModel() {
    private val _uiState = mutableStateOf(LoginState())
    private val _client = TodoClient.getClient()
    val uiState: State<LoginState> = _uiState

    val validationEvent = MutableSharedFlow<ValidationEvent>()

    suspend fun login(email: String, password: String): String? {
        return try {
            val response = _client.login(AuthBody(email, password))
            response.data.token
        } catch (error: Exception) {
            Log.e("LoginError", error.message.toString())
            null
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UpdateEmail -> {
                _uiState.value = _uiState.value.copy(
                    email = event.email
                )
            }
            is LoginEvent.UpdatePassword -> {
                _uiState.value = _uiState.value.copy(
                    password = event.password
                )
            }
            is LoginEvent.ValidateFields -> {
                validateFields()
            }
        }
    }

    private fun validateFields() {
        val emailResult = Validator.validateEmail(_uiState.value.email)
        val passwordResult = Validator.validateLength(_uiState.value.password)

        val hasError = listOf(
            emailResult,
            passwordResult
        ).any { !it.status }

        _uiState.value = _uiState.value.copy(
            hasEmailError = !emailResult.status,
            hasPasswordError = !passwordResult.status
        )

        viewModelScope.launch {
            if (hasError) {
                validationEvent.emit(ValidationEvent.Failed)
            } else validationEvent.emit(ValidationEvent.Success)
        }
    }
}