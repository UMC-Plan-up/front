package com.example.planup.login.ui

import androidx.lifecycle.ViewModel
import com.example.planup.main.user.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    userRepository: UserRepository
): ViewModel() {

}