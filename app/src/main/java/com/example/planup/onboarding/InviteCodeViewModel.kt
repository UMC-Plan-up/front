package com.example.planup.onboarding

import androidx.lifecycle.ViewModel
import com.example.planup.main.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class InviteCodeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _inviteCode: MutableStateFlow<String> = MutableStateFlow("")
    val inviteCode: StateFlow<String> = _inviteCode


}