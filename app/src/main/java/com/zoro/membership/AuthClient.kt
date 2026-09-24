package com.zoro.membership

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object AuthClient {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private const val BASE_URL = BuildConfig.SUPABASE_URL
    private const val ANON_KEY = BuildConfig.SUPABASE_ANON_KEY
    private const val PREFS_NAME = "zoro_session"

    @Serializable
    data class AuthUser(val id: String, val email: String? = null)

    @Serializable
    data class AuthResponse(
        val access_token: String? = null,
        val user: AuthUser? = null,
        val error_description: String? = null,
        val msg: String? = null
    )

    fun saveSession(context: Context, userId: String, email: String?, token: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString("user_id", userId)
            .putString("email", email)
            .putString("access_token", token)
            .apply()
    }

    fun getSession(context: Context): AuthUser? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val id = prefs.getString("user_id", null) ?: return null
        val email = prefs.getString("email", null)
        return AuthUser(id, email)
    }

    fun getAccessToken(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString("access_token", null)

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }

    suspend fun signUp(context: Context, email: String, password: String): AuthUser =
        authCall(context, "signup", email, password)

    suspend fun signIn(context: Context, email: String, password: String): AuthUser =
        authCall(context, "token?grant_type=password", email, password)

    private suspend fun authCall(context: Context, path: String, email: String, password: String): AuthUser =
        withContext(Dispatchers.IO) {
            val payload = """{"email":"$email","password":"$password"}"""
            val body = payload.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL/auth/v1/$path")
                .header("apikey", ANON_KEY)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: "{}"
                val parsed = json.decodeFromString<AuthResponse>(responseBody)
                if (!response.isSuccessful || parsed.user == null || parsed.access_token == null) {
                    throw Exception(parsed.error_description ?: parsed.msg ?: "Authentication failed")
                }
                saveSession(context, parsed.user.id, parsed.user.email, parsed.access_token)
                parsed.user
            }
        }
}
