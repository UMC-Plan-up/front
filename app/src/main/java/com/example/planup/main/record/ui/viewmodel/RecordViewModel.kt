package com.example.planup.main.record.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planup.main.record.data.BadgeDTO
import com.example.planup.main.record.data.NotificationDTO
import com.example.planup.main.record.data.WeeklyReportResult
import com.example.planup.main.record.ui.repository.RecordRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository
): ViewModel(){
    val defaultCheering = "이번 주도 화이팅! 꾸준함이 실력을 만듭니다 💪"
    private val _cheering = MutableStateFlow(defaultCheering)
    val cheering: StateFlow<String> = _cheering
    private val _badgeList = MutableStateFlow<List<BadgeDTO>>(emptyList())
    val badgeList: StateFlow<List<BadgeDTO>> = _badgeList
    private val _notificationList = MutableStateFlow<List<NotificationDTO>>(emptyList())
    val notificationList: StateFlow<List<NotificationDTO>> = _notificationList
    private var userId = 0


    fun loadWeeklyGoalReport(
        onCallBack: (result: ApiResult<WeeklyReportResult>) -> Unit
    ) {
        viewModelScope.launch {
            recordRepository.loadWeeklyGoalReport(userId)
                .onSuccess { result ->
                    _cheering.value = result.cheering?.takeIf { it.isNotBlank() } ?: defaultCheering
                    _badgeList.value = result.badgeDTOList
                    onCallBack(ApiResult.Success(result))
                }.onFailWithMessage { message ->
                    Log.d("loadWeeklyGoalReport", "Fail: $message")
                    onCallBack(ApiResult.Fail(message))
                }
        }
    }

    fun loadMonthlyReport(
        year: Int, month: Int) {
        viewModelScope.launch {
            recordRepository.loadMonthlyReport(userId, year, month)
                .onSuccess { result ->
                    Log.d("loadMonthlyReport", "Success: $result")
                }.onFailWithMessage { message ->
                    Log.w("loadMonthlyReport", "monthly weeks FAIL: http=${message}")
                }
        }
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            recordRepository.getUserInfo()
                .onSuccess { result ->
                    userId = result.id
                }
                .onFailWithMessage { message ->
                    Log.d("loadUserInfo", "Fail: $message")
                }
        }
    }

    fun loadNotification() {
        viewModelScope.launch {
            recordRepository.loadNotification(userId)
        }
    }


}