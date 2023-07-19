package com.example.task_manage_app.screens.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.task_manage_app.api.TodoClient
import com.example.task_manage_app.models.PatchTaskBody
import com.example.task_manage_app.models.PostTaskBody
import com.example.task_manage_app.models.Task
import org.json.JSONObject

class HomeViewModel: ViewModel() {
    private val _todoList = mutableStateOf(mutableStateListOf<Task>())
    private val _client = TodoClient.getClient()
    val todoList: State<List<Task>> = _todoList

    suspend fun fetchTasks(token: String) {
        try {
            val response = _client.fetchTasks(token)
            _todoList.value = response.data.toMutableStateList()
        } catch (error: Exception) {
            Log.e("FetchTasksError", error.message.toString())
        }
    }

    suspend fun addTask(token: String, description: String) {
        try {
            val response = _client.postTask(token, PostTaskBody(description))
            if (response.success) {
                _todoList.value.add(response.data)
            }
        } catch (error: Exception) {
            Log.e("AddTaskError", error.message.toString())
        }
    }

    suspend fun markAsCompleted(token: String, task: Task, value: Boolean) {
        try {
            _client.patchTask(token, task.id, PatchTaskBody(value))
            val index = _todoList.value.indexOf(task)
            _todoList.value[index] = _todoList.value[index].copy(
                completed = value
            )
        } catch (error: Exception) {
            Log.e("UpdateTaskError", error.message.toString())
        }
    }
}