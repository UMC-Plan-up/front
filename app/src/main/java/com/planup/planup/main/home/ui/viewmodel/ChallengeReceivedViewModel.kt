package com.planup.planup.main.home.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.planup.planup.network.repository.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChallengeReceivedViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val challengeRepository: ChallengeRepository
): ViewModel() {
    val challengeId = savedStateHandle.get<Int>("challengeId")
}