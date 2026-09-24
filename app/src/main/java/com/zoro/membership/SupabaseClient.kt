package com.zoro.membership

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Minimal Supabase REST wrapper. `apikey` is always the public anon key
 * (required by Supabase on every request). `Authorization` is the user's
 * real access token when they're logged in, or the anon key otherwise —
 * this is what lets Row Level Security policies correctly identify who's
 * asking, instead of every request looking anonymous even after login.
 */
object SupabaseClient {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private const val BASE_URL = BuildConfig.SUPABASE_URL
    private const val ANON_KEY = BuildConfig.SUPABASE_ANON_KEY

    private fun authHeader(userToken: String?) = userToken ?: ANON_KEY

    suspend fun getCategories(userToken: String? = null): List<Category> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$BASE_URL/rest/v1/categories?select=*&order=sort_order")
            .header("apikey", ANON_KEY)
            .header("Authorization", "Bearer ${authHeader(userToken)}")
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string()
            if (!response.isSuccessful) {
                throw Exception("HTTP ${response.code}: ${body ?: "no body"}")
            }
            json.decodeFromString(body ?: "[]")
        }
    }

    suspend fun getMerchants(categoryId: String, userToken: String? = null): List<Merchant> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$BASE_URL/rest/v1/merchants?select=*&category_id=eq.$categoryId&status=eq.approved")
            .header("apikey", ANON_KEY)
            .header("Authorization", "Bearer ${authHeader(userToken)}")
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string()
            if (!response.isSuccessful) {
                throw Exception("HTTP ${response.code}: ${body ?: "no body"}")
            }
            json.decodeFromString(body ?: "[]")
        }
    }

    suspend fun getOffers(merchantId: String, userToken: String? = null): List<Offer> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$BASE_URL/rest/v1/offers?select=*&merchant_id=eq.$merchantId&status=eq.active")
            .header("apikey", ANON_KEY)
            .header("Authorization", "Bearer ${authHeader(userToken)}")
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

@Serializable
data class Merchant(
    val id: String,
    val display_name_i18n: Map<String, String> = emptyMap(),
    val bio_i18n: Map<String, String> = emptyMap()
) {
    fun nameFor(languageCode: String): String =
        display_name_i18n[languageCode] ?: display_name_i18n["en"] ?: "Unnamed"

    fun bioFor(languageCode: String): String =
        bio_i18n[languageCode] ?: bio_i18n["en"] ?: ""
}

@Serializable
data class Offer(
    val id: String,
    val title_i18n: Map<String, String> = emptyMap(),
    val terms_i18n: Map<String, String> = emptyMap(),
    val discount_type: String? = null,
    val value: Double? = null,
    val min_spend_usd_cents: Int? = null
) {
    fun titleFor(languageCode: String): String =
        title_i18n[languageCode] ?: title_i18n["en"] ?: "Offer"

    fun termsFor(languageCode: String): String =
        terms_i18n[languageCode] ?: terms_i18n["en"] ?: ""

    fun summary(): String = when (discount_type) {
        "percent" -> "${value?.toInt() ?: 0}% off"
        "fixed" -> "$${value?.toInt() ?: 0} off"
        "bogo" -> "Buy 1 Get 1 Free"
        else -> "Special offer"
    }

    fun condition(): String? =
        min_spend_usd_cents?.let { "Spend $${it / 100}+ to unlock" }
}
