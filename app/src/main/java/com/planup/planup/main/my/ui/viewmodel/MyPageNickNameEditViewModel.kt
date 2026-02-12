package com.planup.planup.main.my.ui.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planup.planup.main.user.domain.UserNameAlreadyExistException
import com.planup.planup.main.user.domain.UserRepository
import com.planup.planup.network.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageNickNameEditViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val charLimit = 20

    var newName by mutableStateOf("")
        private set

    fun updateName(name: String) {
        newName = name
    }

    var errorMsg by mutableStateOf("")
        private set

    fun clearErrorMsg() {
        errorMsg = ""
    }

    val isNameError by derivedStateOf {
        newName.length >= charLimit
    }

    var isNameAlreadyError by mutableStateOf(false)

    val isError by derivedStateOf {
        isNameError || isNameAlreadyError
    }

    val completeEnabled by derivedStateOf { newName.isNotEmpty() && !isError }

    fun changeNickname(
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val result = userRepository.changeNickName(newName)
            isNameAlreadyError = false
            when (result) {
                is ApiResult.Success -> {
                    onSuccess()
                }

                is ApiResult.Fail -> {
                    errorMsg = result.message
                }

                is ApiResult.Exception -> {
                    val exception = result.error
                    if (exception is UserNameAlreadyExistException) {
                        isNameAlreadyError = true
                    }
                    errorMsg = exception.message ?: "알 수 없는 에러"
                }

                is ApiResult.Error -> {
                    errorMsg = result.message
                }
            }
        }
    }
}