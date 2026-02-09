package com.example.planup.main.record.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.goal.item.GoalPhotoResult
import com.example.planup.main.goal.item.GoalPhotosResponse
import com.example.planup.main.record.data.CommentDto
import com.example.planup.main.record.data.DailyAchievementRateDto
import com.example.planup.main.record.data.ReportUserDto
import com.example.planup.main.record.data.ThreeWeekAchievementRateDto
import com.example.planup.main.record.ui.repository.RecordGoalReportRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.toInt

@HiltViewModel
class RecordGoalReportViewModel @Inject constructor(
    private val repository: RecordGoalReportRepository
): ViewModel() {
    private var userId = 0
    private var goalReportId = 0
    private var goalId = 0
    private val _goalTitle = MutableStateFlow("")
    val goalTitle: StateFlow<String> = _goalTitle
    private val _threeWeekAchievementRate = MutableStateFlow(
        ThreeWeekAchievementRateDto(0, 0, 0)
    )
    val threeWeekAchievementRate: StateFlow<ThreeWeekAchievementRateDto> = _threeWeekAchievementRate
    private val _dailyAchievementRate = MutableStateFlow(
        DailyAchievementRateDto(
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0
        )
    )
    val dailyAchievementRate: StateFlow<DailyAchievementRateDto> = _dailyAchievementRate
    private val _reportUsers = MutableStateFlow<List<ReportUserDto>>(emptyList())
    val reportUsers: StateFlow<List<ReportUserDto>> = _reportUsers
    private val _comments = MutableStateFlow<List<CommentDto>>(emptyList())
    val comments: StateFlow<List<CommentDto>> = _comments

    fun fetchGoalReport(userId: Int, goalReportId: Int) {
        this.userId = userId
        this.goalReportId = goalReportId
    }

    private val _photos = MutableStateFlow<List<GoalPhotoResult>>(emptyList())
    val photos: StateFlow<List<GoalPhotoResult>> = _photos

    fun loadWeeklyGoalReport() {
        viewModelScope.launch {
            repository.loadWeeklyGoalReport(userId, goalReportId)
                .onSuccess { result ->
                    _goalTitle.value = result.goalTitle
                    _dailyAchievementRate.value = result.dailyAchievementRate
                    _threeWeekAchievementRate.value = result.threeWeekAchievementRate
                    _reportUsers.value = result.reportUsers
                    _comments.value = result.comments
                    goalId = result.goalId.toInt() //TODO : 백엔드에 수정 요청
                }.onFailWithMessage {
                    Log.d("RecordFail", "loadWeeklyGoalReport Error : $it")
                }
        }
    }

    fun loadPhotos() {
        viewModelScope.launch {
            repository.loadGoalPhotos(goalId)
                .onSuccess { result ->
                    _photos.value = result
                }.onFailWithMessage {
            Log.d("RecordFail", "loadPhotos Error : $it")
            }
        }
    }

}