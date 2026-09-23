package com.zoro.membership

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Minimal Supabase REST wrapper. Uses the public "anon" key, which is safe
 * to ship in the app — real data protection comes from the Row Level
 * Security policies defined in the database (see 001_init_schema.sql).
 */
object SupabaseClient {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private const val BASE_URL = BuildConfig.SUPABASE_URL
    private const val ANON_KEY = BuildConfig.SUPABASE_ANON_KEY

    suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$BASE_URL/rest/v1/categories?select=*&order=sort_order")
            .header("apikey", ANON_KEY)
            .header("Authorization", "Bearer $ANON_KEY")
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string()
            if (!response.isSuccessful) {
                throw Exception("HTTP ${response.code}: ${body ?: "no body"}")
            }
            json.decodeFromString(body ?: "[]")
        }
    }
}

@Serializable
data class Category(
    val id: String,
    val name_i18n: Map<String, String> = emptyMap(),
    val icon: String? = null,
    val sort_order: Int? = null
) {
    fun nameFor(languageCode: String): String =
        name_i18n[languageCode] ?: name_i18n["en"] ?: "Unnamed"
}
