package com.example.planup.main.my.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class MyPageProfileEditViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val userRepository: UserRepository
) : ViewModel() {

    /**
     * PhotoPicker로 부터 들어온 경우..
     *
     * @param imageUri
     * @param onSuccess
     * @param onFail
     */
    fun setProfileImageByPicker(
        imageUri: Uri,
        onSuccess: () -> Unit,
        onFail: (message: String) -> Unit
    ) {
        viewModelScope.launch {
            val file = createImageFile(context)
            //내부 캐시 폴더에 저장 시도.
            runCatching {
                context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        val buffer = ByteArray(4 * 1024)
                        var read: Int
                        while (inputStream.read(buffer).also { read = it } != -1) {
                            outputStream.write(buffer, 0, read)
                        }
                        outputStream.flush()
                    }
                }
            }.onFailure {
                if (it is CancellationException) {
                    throw it
                }
                onFail("Fail to Success Image")
            }.onSuccess {
                //실패 없이 동작 했다면
                userRepository.setProfileImage(file)
                    .onSuccess {
                        onSuccess()
                    }
                    .onFailWithMessage(onFail)
            }
        }
    }

    fun setProfileImageCamera(
        onSuccess: () -> Unit,
        onFail: (message: String) -> Unit
    ) {
        if (cameraUri == null) {

            return
        } else {
            viewModelScope.launch {
                val file = createImageFile(context)
                //내부 캐시 폴더에 저장 시도.
                runCatching {
                    context.contentResolver.openInputStream(cameraUri!!)?.use { inputStream ->
                        FileOutputStream(file).use { outputStream ->
                            val buffer = ByteArray(4 * 1024)
                            var read: Int
                            while (inputStream.read(buffer).also { read = it } != -1) {
                                outputStream.write(buffer, 0, read)
                            }
                            outputStream.flush()
                        }
                    }
                }.onFailure {
                    if (it is CancellationException) {
                        throw it
                    }
                    onFail("Fail to Success Image")
                }.onSuccess {
                    //실패 없이 동작 했다면
                    userRepository.setProfileImage(file)
                        .onSuccess {
                            onSuccess()
                        }
                        .onFailWithMessage(onFail)
                }
            }
        }
    }


    //이미지 임시 파일 생성
    private fun createImageFile(context: Context): File {
        val timeStamp = System.currentTimeMillis()
        val imageFileName = "@${timeStamp}_"
        return File.createTempFile(
            imageFileName,
            ".png",
            context.cacheDir
        ).also {
            //앱 종료시 삭제 예약
            it.deleteOnExit()
        }
    }

    private var cameraUri: Uri? = null

    fun makeCameraTempFileUri(): Uri {
        val file = createImageFile(context)
        cameraUri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        return cameraUri!!
    }
}