package com.example.fitnessai.data

import android.content.Context
import com.example.fitnessai.model.*
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class ApiClient(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    var baseUrl: String
        get() = context.getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
            .getString("backend_url", "http://localhost:8000/api/v1") ?: "http://localhost:8000/api/v1"
        set(value) = context.getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
            .edit().putString("backend_url", value).apply()

    var authToken: String?
        get() = context.getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
            .getString("auth_token", null)
        set(value) = context.getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
            .edit().putString("auth_token", value).apply()

    fun postWorkoutLog(request: WorkoutLogRequest): WorkoutLogResponse? {
        return try {
            val body = gson.toJson(request).toRequestBody(jsonType)
            val reqBuilder = Request.Builder()
                .url("$baseUrl/workout/log")
                .post(body)

            authToken?.let { reqBuilder.addHeader("Authorization", "Bearer $it") }

            client.newCall(reqBuilder.build()).execute().use { res ->
                if (res.isSuccessful) {
                    val respBody = res.body?.string()
                    gson.fromJson(respBody, WorkoutLogResponse::class.java)
                } else null
            }
        } catch (_: Exception) {
            null
        }
    }
}
