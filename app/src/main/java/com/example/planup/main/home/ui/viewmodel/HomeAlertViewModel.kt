package com.example.planup.main.home.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.home.ui.HomeAlertRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.dto.notification.NotificationResult
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
    private var userId: Int = 0
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


    fun loadUserId() {
        viewModelScope.launch {
            try {
                val response = repo.loadUserInfo()
                if (response is ApiResult.Success) {
                    val result = response.data.result
                    userId = result.id
                }
            } catch (e: CancellationException) {
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    // 에러 이벤트
    private val _error = MutableSharedFlow<Throwable>()
    val error: SharedFlow<Throwable> = _error

    /** 🔥 API 호출 진입점 */
    fun loadNotifications(receiverId: Int) {
        viewModelScope.launch {
            try {
                coroutineScope {
                    val achieveDeferred = async {
                        repo.loadNotificationType(receiverId, "ACHIEVE")
                    }
                    val receiveDeferred = async {
                        repo.loadNotificationType(receiverId, "RECEIVE")
                    }
                    val challengeDeferred = async {
                        repo.loadNotificationType(receiverId, "CHALLENGE")
                    }

                    (achieveDeferred.await() as? ApiResult.Success)?.let {
                        _goalList.value = it.data
                    }

                    (receiveDeferred.await() as? ApiResult.Success)?.let {
                        _feedbackList.value = it.data
                    }

                    (challengeDeferred.await() as? ApiResult.Success)?.let {
                        _challengeList.value = it.data
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.emit(e)
            }
        }
    }

    fun selectCategory(category: NotificationCategory) {
        _selectedCategory.value = category
    }
}

enum class NotificationCategory {
    GOAL, FEEDBACK, CHALLENGE
}
