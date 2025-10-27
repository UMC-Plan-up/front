package com.example.planup.main.my.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageDeleteAccountViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun withDraw(
        reason : String,
        onSuccess :() -> Unit,
        onFail :(msg: String) -> Unit
    ) {
        viewModelScope.launch {
            userRepository.withdraw(reason)
                .onSuccess {
                    onSuccess()
                }
                .onFailWithMessage(onFail)
        }
    }
}