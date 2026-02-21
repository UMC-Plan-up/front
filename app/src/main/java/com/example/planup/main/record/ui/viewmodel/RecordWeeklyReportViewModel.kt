package com.example.planup.main.record.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.goal.data.ChallengeFriend
import com.example.planup.main.record.data.Badge
import com.example.planup.main.record.data.DailyRecord
import com.example.planup.main.record.data.DetailWeeklyReportResult
import com.example.planup.main.record.data.GoalReport
import com.example.planup.main.record.data.WeeklyReportResult
import com.example.planup.main.record.ui.repository.RecordRepository
import com.example.planup.main.record.ui.repository.RecordWeeklyReportRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.data.ChallengeFriends
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import javax.inject.Inject

@HiltViewModel
class RecordWeeklyReportViewModel @Inject constructor(
    private val reportRepository: RecordWeeklyReportRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    //날짜 기록
    // 주차 계산용(ISO: 월요일 시작)
    private val weekFields = WeekFields.ISO
    private val _userId = MutableStateFlow(0)
    val userId: StateFlow<Int> = _userId
    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname
    private val _year =
        MutableStateFlow(savedStateHandle["ARG_YEAR"] ?: 2026)
    val year: StateFlow<Int> = _year
    private val _month =
        MutableStateFlow(savedStateHandle["ARG_MONTH"] ?: 1)
    val month: StateFlow<Int> = _month
    private val _week =
        MutableStateFlow(savedStateHandle["ARG_WEEK"] ?: 1)
    val week: StateFlow<Int> = _week


    private val defaultNextMsg = "아직 데이터가 없습니다. 새로운 목표를 향해 달려가 볼까요?"
    private val _nextMsg = MutableStateFlow(defaultNextMsg)
    val nextMsg: StateFlow<String> = _nextMsg
    private val _goalReportList = MutableStateFlow<List<GoalReport>>(emptyList())
    val goalReportList: StateFlow<List<GoalReport>> = _goalReportList
    private val _dailyList = MutableStateFlow<List<DailyRecord>>(emptyList())
    val dailyList: StateFlow<List<DailyRecord>> = _dailyList
    private val _badgeList = MutableStateFlow<List<Badge>>(emptyList())
    val badgeList: StateFlow<List<Badge>> = _badgeList
    private val _myAvgAchievementPercent = MutableStateFlow(0)
    val myAvgAchievementPercent: StateFlow<Int> = _myAvgAchievementPercent

    private val _challengeFriendList = MutableStateFlow<List<ChallengeFriends>>(emptyList())
    val challengeFriendList: StateFlow<List<ChallengeFriends>> = _challengeFriendList


    fun loadWeeklyReport(
        onCallBack: (result: ApiResult<DetailWeeklyReportResult>) -> Unit
    ) {
        viewModelScope.launch {
            reportRepository.loadWeeklyReport(
                _year.value, _month.value, _week.value, _userId.value
            ).onSuccess { result ->
                    _nextMsg.value = result.nextGoalMessage
                    _goalReportList.value = result.goalReports
                    _dailyList.value = result.dailyRecordList
                    _badgeList.value = result.badgeList
                    _myAvgAchievementPercent.value = if (_goalReportList.value.isNotEmpty()) {
                        _goalReportList.value.map { it.achievementRate ?: 0 }.average().toInt().coerceIn(0, 100)
                    } else 0
                    onCallBack(ApiResult.Success(result))
                }.onFailWithMessage {
                    onCallBack(ApiResult.Fail(it))
            }
        }
    }

    fun loadChallengeFriends(
        onCallBack: (result: ApiResult<List<ChallengeFriends>>) -> Unit
    ) {
        viewModelScope.launch {
            reportRepository.loadChallengeFriend(_userId.value)
                .onSuccess { result ->
                    _challengeFriendList.value = result
                    onCallBack(ApiResult.Success(result))
                }.onFailWithMessage {
                    onCallBack(ApiResult.Fail(it))
                }
        }
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            reportRepository.getUserInfo()
                .onSuccess { result ->
                    _userId.value = result.id
                    _nickname.value = result.nickname!!
                }
                .onFailWithMessage { message ->
                    Log.d("loadUserInfo", "Fail: $message")
                }
        }
    }

    fun moveToPreviousWeek() {
        if (_week.value > 1) {
            _week.value--
        } else {
            if (_month.value == 1) {
                _year.value--; _month.value = 12
            } else _month.value--
            _week.value = maxWeeksInMonth(_year.value, _month.value)
        }
    }

    fun moveToNextWeek() {
        val maxWeeks = maxWeeksInMonth(_year.value, _month.value)
        if (_week.value < maxWeeks) {
            _week.value++
        } else {
            if (_month.value == 12) { _year.value++; _month.value = 1 } else _month.value++
            _week.value = 1
        }
    }

    private fun maxWeeksInMonth(year: Int, month: Int): Int {
        val ym = YearMonth.of(year, month)
        var max = 0
        for (d in 1..ym.lengthOfMonth()) {
            val week = LocalDate.of(year, month, d).get(weekFields.weekOfMonth())
            if (week > max) max = week
        }
        return max.coerceAtLeast(1)
    }

}