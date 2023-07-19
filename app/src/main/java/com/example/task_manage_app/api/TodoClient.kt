package com.example.task_manage_app.api

import com.example.task_manage_app.models.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface TodoClient {
    @POST("users/login")
    suspend fun login(@Body body: AuthBody): AuthDefaultResponse

    @GET("tasks")
    suspend fun fetchTasks(@Header("authorization") token: String): GetTasksResponse

    @POST("tasks")
    suspend fun postTask(
        @Header("authorization") token: String,
        @Body body: PostTaskBody
    ): DefaultTaskResponse

    @PATCH("tasks/{id}")
    suspend fun patchTask(
        @Header("authorization") token: String,
        @Path("id") id: String,
        @Body body: PatchTaskBody
    ): DefaultTaskResponse

    companion object {
        private var client: TodoClient? = null
        private const val baseUrl: String = "https://4c6a-186-98-0-187.ngrok-free.app"

        fun getClient(): TodoClient {
            if (client !== null) {
                return client!!
            }

            client = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TodoClient::class.java)
            return client!!
        }
    }
}
