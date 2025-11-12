package com.example.planup.main.my.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import com.example.planup.util.ImageResizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imageResizer: ImageResizer
) : ViewModel() {

    /**
     * PhotoPicker로 부터 들어온 경우..
     *
     * @param imageUri
     * @param onSuccess
     * @param onFail
     */
    fun setProfileImageByPicker(
        imageData: Any,
        onSuccess: () -> Unit,
        onFail: (message: String) -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                imageResizer.saveToTempFile(imageData)!!
            }.onFailure {
                if (it is CancellationException) {
                    throw it
                }
                onFail("Fail to Success Image")
            }.onSuccess { file ->
                Log.d("MyPageProfileEditViewModel", "setProfileImageByPicker: $file")
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