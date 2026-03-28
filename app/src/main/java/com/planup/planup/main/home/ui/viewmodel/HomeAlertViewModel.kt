package com.planup.planup.main.home.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planup.planup.main.home.ui.repository.HomeAlertRepository
import com.planup.planup.network.ApiResult
import com.planup.planup.network.dto.notification.NotificationResult
import com.planup.planup.network.onFailWithMessage
import com.planup.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@HiltViewModel
class HomeAlertViewModel @Inject constructor(
    private val repo: HomeAlertRepository
) : ViewModel() {

    // 원본 데이터
    private val _goalList = MutableStateFlow<List<NotificationResult>>(emptyList())
    private val _feedbackList = MutableStateFlow<List<NotificationResult>>(emptyList())
    private val _challengeList = MutableStateFlow<List<NotificationResult>>(emptyList())
    var userId: Int = 0
    // 현재 선택된 카테고리
    private val _selectedCategory =
        MutableStateFlow(NotificationCategory.GOAL)
    val selectedCategory: StateFlow<NotificationCategory> = _selectedCategory


    // UI에 노출할 리스트
    val notificationList: StateFlow<List<NotificationResult>> =
        combine(
            _selectedCategory,
            _goalList,
            _feedbackList,
            _challengeList
        ) { category, goal, feedback, challenge ->
            when (category) {
                NotificationCategory.GOAL -> goal
                NotificationCategory.FEEDBACK -> feedback
                NotificationCategory.CHALLENGE -> challenge
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )


//    fun loadUserId() {
//        viewModelScope.launch {
//            try {
//                val response = repo.loadUserInfo()
//                if (response is ApiResult.Success) {
//                    val result = response.data
//                    userId = result.id
//                }
//            } catch (e: CancellationException) {
//            } catch (e: Exception){
//                e.printStackTrace()
//            }
//        }
//    }
    fun loadNotifications() {
        viewModelScope.launch {
            loadNotifications(receiverId = userId)
        }
    }

    // 에러 이벤트
    private val _error = MutableSharedFlow<Throwable>()
    val error: SharedFlow<Throwable> = _error

    /** 🔥 API 호출 진입점 */
    fun loadNotifications(receiverId: Int) {
        viewModelScope.launch {
            Log.d("HomeAlertViewModel", "loadNotifications: loading")
            if (receiverId == 0) {
                Log.d("HomeAlertViewModel", "loadNotifications: receiverId is 0")
                return@launch
            }
            when(_selectedCategory.value){
                NotificationCategory.GOAL -> {
                    repo.loadNotificationType(receiverId, "GOAL")
                        .onSuccess {
                            Log.d("HomeAlertViewModel", "loadNotifications: achieveDeferred $it")
                            _goalList.value = it
                        }
                        .onFailWithMessage { it ->
                            Log.d("HomeAlertViewModel", "loadNotifications: achieveDeferred $it")
                        }
                }
                NotificationCategory.FEEDBACK -> {
                    repo.loadNotificationType(receiverId, "REACTION")
                        .onSuccess {
                            Log.d("HomeAlertViewModel", "loadNotifications: receiveDeferred $it")
                            _feedbackList.value = it
                        }
                        .onFailWithMessage { it ->
                            Log.d("HomeAlertViewModel", "loadNotifications: receiveDeferred $it")
                        }
                }
                NotificationCategory.CHALLENGE -> {
                    repo.loadNotificationType(receiverId, "CHALLENGE")
                        .onSuccess {
                            Log.d("HomeAlertViewModel", "loadNotifications: challengeDeferred $it")
                            _challengeList.value = it
                        }
                        .onFailWithMessage { it ->
                            Log.d("HomeAlertViewModel", "loadNotifications: challengeDeferred $it")
                        }
                }
            }
        }
    }

    fun onClicked(item: NotificationResult) {
        viewModelScope.launch {

        }
    }

    fun selectCategory(category: NotificationCategory) {
        _selectedCategory.value = category
    }
}

enum class NotificationCategory {
    GOAL, FEEDBACK, CHALLENGE
}
