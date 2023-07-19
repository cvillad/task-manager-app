package com.example.task_manage_app.models

data class AuthBody(
    val email: String,
    val password: String
)

data class AuthDefaultResponse(
    val success: Boolean,
    val data: AuthData
)

data class AuthData(
    val token: String
)