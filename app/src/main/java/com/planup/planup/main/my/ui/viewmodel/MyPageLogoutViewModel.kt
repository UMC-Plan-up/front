package com.planup.planup.main.my.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planup.planup.main.user.domain.UserRepository
import com.planup.planup.network.onFailWithMessage
import com.planup.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageLogoutViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun logout(
        onSuccess :() -> Unit,
        onFail :(msg: String) -> Unit
    ) {
        viewModelScope.launch {
            userRepository.logout()
                .onSuccess {
                    onSuccess()
                }
                .onFailWithMessage(onFail)
        }
    }
}