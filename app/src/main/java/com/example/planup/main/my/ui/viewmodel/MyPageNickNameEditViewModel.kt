package com.example.planup.main.my.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageNickNameEditViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun changeNickname(
        nickName : String,
        onSuccess :() -> Unit
    ) {
        viewModelScope.launch {
            val result = userRepository.changeNickName(nickName)
            when(result) {
                is ApiResult.Success -> {
                    onSuccess()
                }
                is ApiResult.Fail -> {

                }
                is ApiResult.Exception -> {

                }

                is ApiResult.Error -> {

                }
            }
        }
    }
}