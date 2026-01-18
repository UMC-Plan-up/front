package com.example.planup.onboarding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.database.TokenSaver
import com.example.planup.main.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteCodeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _inviteCode: MutableStateFlow<String> = MutableStateFlow("")
    val inviteCode: StateFlow<String> = _inviteCode

    init {
        viewModelScope.launch {
            val code = userRepository.getInviteCode()
            println("inviteCode = $code")
            _inviteCode.update { code }
        }
    }


}