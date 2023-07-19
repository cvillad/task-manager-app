package com.example.task_manage_app.validations

import android.util.Patterns

data class ValidationResult(
    val status: Boolean = true
)

sealed class ValidationEvent {
    object Success: ValidationEvent()
    object Failed: ValidationEvent()
}

object Validator {
    fun validateEmail(email: String): ValidationResult {
        return ValidationResult(email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    fun validateLength(value: String, minLength: Int = 6): ValidationResult {
        return ValidationResult(value.length >= minLength)
    }
}
