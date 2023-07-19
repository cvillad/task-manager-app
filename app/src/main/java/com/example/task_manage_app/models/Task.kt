package com.example.task_manage_app.models

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("_id") val id: String,
    val description: String,
    val completed: Boolean = false
)

data class GetTasksResponse(
    val success: Boolean,
    val data: List<Task>
)

data class PostTaskBody(
    val description: String
)

data class PatchTaskBody(
    val completed: Boolean
)

data class DefaultTaskResponse(
    val success: Boolean,
    val data: Task
)
