package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageStorageHelper {

    private fun getImageDirectory(context: Context): File {
        val dir = File(context.filesDir, "husaini_images")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun saveUriToFile(context: Context, sourceUri: Uri, prefix: String): String? {
        return try {
            val dir = getImageDirectory(context)
            val fileName = "${prefix}_${UUID.randomUUID()}.jpg"
            val destFile = File(dir, fileName)
            
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                // Decode scaled down to preserve memory
                val options = BitmapFactory.Options().apply {
                    inSampleSize = calculateInSampleSize(context, sourceUri, 1200, 1200)
                }
                val bitmap = BitmapFactory.decodeStream(
                    context.contentResolver.openInputStream(sourceUri),
                    null,
                    options
                )
                if (bitmap != null) {
                    FileOutputStream(destFile).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    }
                    destFile.absolutePath
                } else {
                    // Fallback to raw copy
                    FileOutputStream(destFile).use { out ->
                        input.copyTo(out)
                    }
                    destFile.absolutePath
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createTempCameraFile(context: Context, prefix: String): Pair<File, Uri> {
        val dir = getImageDirectory(context)
        val file = File(dir, "${prefix}_camera_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return Pair(file, uri)
    }

    private fun calculateInSampleSize(
        context: Context,
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1
        try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
            val height = options.outHeight
            val width = options.outWidth

            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2
                while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2
                }
            }
        } catch (e: Exception) {
            inSampleSize = 1
        }
        return inSampleSize
    }
}
