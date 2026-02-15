package com.planup.planup.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.scale
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

@Singleton
class ImageResizer @Inject constructor(
    @ApplicationContext private val context: Context
)  {
    companion object {
        private const val MAX_WIDTH = 300
        private const val MAX_HEIGHT = 300
    }

    /**
     * Uri 또는 Bitmap을 받아서 리사이즈 후 임시 파일로 저장
     */
    suspend fun saveToTempFile(input: Any): File? = withContext(Dispatchers.IO) {
        val bitmap = when (input) {
            is Uri -> resizeBitmapFromUri(input)
            is Bitmap -> resizeBitmap(input)
            else -> return@withContext null
        }
        bitmap?.let { saveBitmapToTempFile(it) }
    }

    /**
     * Bitmap 저장
     */
    private fun saveBitmapToTempFile(bitmap: Bitmap): File? {
        return try {
            val file = createTempImageFile()
            FileOutputStream(file).use { output ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Uri 기반 리사이즈
     */
    private fun resizeBitmapFromUri(uri: Uri): Bitmap? {
        // 1. 이미지 크기만 가져오기
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }

        // 2. 샘플 사이즈 계산
        val ratio = calculateRatio(options.outWidth, options.outHeight, MAX_WIDTH, MAX_HEIGHT)
        val targetWidth = (options.outWidth * ratio).toInt()
        val targetHeight = (options.outHeight * ratio).toInt()
        options.inJustDecodeBounds = false
        options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, targetWidth, targetHeight)

        // 3. 샘플링 적용해서 안전하게 디코딩
        val bitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }

        return bitmap?.let { resizeBitmap(it, targetWidth, targetHeight) }
    }

    /**
     * Bitmap 기반 리사이즈
     */
    private fun resizeBitmap(bitmap: Bitmap, targetWidth: Int = MAX_WIDTH, targetHeight: Int = MAX_HEIGHT): Bitmap {
        val ratio = calculateRatio(bitmap.width, bitmap.height, targetWidth, targetHeight)
        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()
        return bitmap.scale(width, height)
    }

    /**
     * 비율 계산
     */
    private fun calculateRatio(origWidth: Int, origHeight: Int, reqWidth: Int, reqHeight: Int): Float {
        return min(reqWidth.toFloat() / origWidth, reqHeight.toFloat() / origHeight).coerceAtMost(1f)
    }

    /**
     * 샘플 사이즈 계산
     */
    private fun calculateInSampleSize(origWidth: Int, origHeight: Int, reqWidth: Int, reqHeight: Int): Int {
        var inSampleSize = 1
        if (origHeight > reqHeight || origWidth > reqWidth) {
            val halfHeight = origHeight / 2
            val halfWidth = origWidth / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * 임시 이미지 파일 생성 (앱 종료 시 자동 삭제)
     */
    private fun createTempImageFile(): File {
        val timeStamp = System.currentTimeMillis()
        val imageFileName = "resized_${timeStamp}_"
        return File.createTempFile(
            imageFileName,
            ".png",
            context.cacheDir
        ).also {
            it.deleteOnExit() // 앱 종료 시 자동 삭제
        }
    }

}