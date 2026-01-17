package com.example.planup.onboarding

import androidx.lifecycle.ViewModel
import com.example.planup.main.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InviteCodeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

}