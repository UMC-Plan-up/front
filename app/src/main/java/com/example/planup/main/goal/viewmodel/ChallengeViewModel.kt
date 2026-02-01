package com.example.planup.main.goal.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.friend.domain.FriendRepository
import com.example.planup.main.goal.domain.GoalRepository
import com.example.planup.main.home.ui.FriendGoalListRepository
import com.example.planup.main.user.domain.UserRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _userNickName = MutableLiveData<Int>()
    val userNickName: Int = _userNickName.value ?: 0

    fun getUserNickName(action: (Int) -> Unit) = viewModelScope.launch {
        userRepository.getUserInfo().onSuccess {
            Log.d("Challenge", it.toString())
            _userNickName.value = it.id
            action(it.id)
        }.onFailWithMessage {
            it
        }
    }

}