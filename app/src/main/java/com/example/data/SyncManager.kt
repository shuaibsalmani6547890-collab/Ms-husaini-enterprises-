package com.example.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

object SyncManager {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(List::class.java, CustomerRecord::class.java)
    private val adapter = moshi.adapter<List<CustomerRecord>>(listType)

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    fun exportToJson(records: List<CustomerRecord>): String {
        return adapter.indent("  ").toJson(records)
    }

    fun importFromJson(json: String): List<CustomerRecord> {
        return adapter.fromJson(json) ?: emptyList()
    }

    fun createExportFile(context: Context, records: List<CustomerRecord>): Uri {
        val json = exportToJson(records)
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) exportDir.mkdirs()
        
        val file = File(exportDir, "Husaini_Enterprises_Backup_${System.currentTimeMillis()}.json")
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    suspend fun syncWithWebhook(url: String, records: List<CustomerRecord>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val json = exportToJson(records)
                val body = json.toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()
                
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Result.success("Sync Successful: ${response.code}")
                    } else {
                        Result.failure(Exception("HTTP Error: ${response.code} ${response.message}"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
