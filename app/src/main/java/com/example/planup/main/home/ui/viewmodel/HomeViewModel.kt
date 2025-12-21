package com.example.planup.main.home.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.R
import com.example.planup.main.goal.item.DailyGoalResult
import com.example.planup.main.goal.item.FriendGoalAchievementResult
import com.example.planup.main.goal.item.MyGoalListItem
import com.example.planup.main.home.data.DailyToDo
import com.example.planup.main.home.item.FriendChallengeItem
import com.example.planup.main.home.data.CalendarEvent
import com.example.planup.main.home.ui.HomeRepository
import com.example.planup.network.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel() {

    // daily todo
    private val _dailyToDos = MutableStateFlow<List<DailyToDo>>(emptyList())
    val dailyToDos: StateFlow<List<DailyToDo>> = _dailyToDos

    // friend challenge
    private val _friendChallenges = MutableStateFlow<List<FriendChallengeItem>>(emptyList())
    val friendChallenges: StateFlow<List<FriendChallengeItem>> = _friendChallenges

    // calendar events
    private val _calendarEvents = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val calendarEvents: StateFlow<List<CalendarEvent>> = _calendarEvents

    fun loadMyGoalList(
        onCallBack: (result: ApiResult<List<MyGoalListItem>>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = homeRepository.getMyGoalList()
                if (result is ApiResult.Success) {
                    Log.d("loadMyGoalList", "result: ${result.data}")
                    val dailyList = mutableListOf<DailyToDo>()
                    for (goal in result.data) {
                        val total = homeRepository.loadTotalAchievement(goal.goalId)
                        if(total is ApiResult.Success) {
                            val progress = total.data.totalAchievementRate
                            dailyList.add(DailyToDo(goal.goalName, progress, goal.frequency))
                        }
                    }
                    _dailyToDos.value = dailyList
                }
                Log.d("loadMyGoalList", "result: ${_dailyToDos.value}")
                onCallBack(result)
            } catch (e: CancellationException) {
            }
            catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }
        }
    }
    fun loadFriendChallenges(
        onCallBack: (result: ApiResult<FriendGoalAchievementResult>) -> Unit
    ) {
        viewModelScope.launch {
            Log.d("loadfriendchallenge","loadFriendChallenges")
            val summaries = mutableListOf<FriendChallengeItem>()
            try {
                val friendRes = homeRepository.getFriendSummary()
                Log.d("loadfriendchallenge","friendRes : $friendRes")
                if (friendRes is ApiResult.Success) {
                    val friendList = friendRes.data.friendInfoSummaryList
                    Log.d("loadfriendchallenge","friendlist : $friendList")
                    for (friend in friendList) {
                        val friendId = friend.id
                        val nickname = friend.nickname
                        val goalListRes = homeRepository.getFriendGoalList(friendId)
                        if (goalListRes is ApiResult.Success) {
                            val achievements = mutableListOf<Int>()
                            for (goal in goalListRes.data) {
                                val achieveRes =
                                    homeRepository.getFriendGoalAchievement(friendId, goal.goalId)
                                if (achieveRes is ApiResult.Success) {
                                    achievements.add(achieveRes.data.totalAchievement)
                                }
                                val avg = achievements.average()
                                val top3float = achievements.sortedDescending().take(3).map { it.toFloat() }
                                summaries.add(
                                    FriendChallengeItem(
                                        friendId,
                                        nickname,
                                        "평균 목표 달성률 : $avg",
                                        R.drawable.profile_example,
                                        top3float
                                    )
                                )
                                Log.d("loadfriendchallenge","$summaries")
                                onCallBack(achieveRes)
                            }
                        }
                    }
                }
            } catch (e: CancellationException){
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }
            summaries.add(
                FriendChallengeItem(
                    0,
                    "예시",
                    "평균 목표 달성률 : ",
                    R.drawable.profile_example,
                    listOf(1f, 2f, 3f)
                )
            )
            _friendChallenges.value = summaries
        }
    }

    fun loadDailyGoals(
        date: LocalDate,
        onCallBack: (result: ApiResult<DailyGoalResult>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = homeRepository.getDailyGoal(date)
                if (response is ApiResult.Success) {
                    val events = response.data.verifiedGoals.map { goal ->
                        CalendarEvent(goal.goalName, goal.period ?: "NULL", goal.frequency, date)
                    }
                    _calendarEvents.value = _calendarEvents.value + events
                    onCallBack(response)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }
        }
    }

    fun loadMonthEvents(month: YearMonth) {
        viewModelScope.launch {
            val startDate = month.atDay(1)
            val endDate = month.atEndOfMonth()
            _calendarEvents.value = emptyList() // clear previous
            var current = startDate
            while (!current.isAfter(endDate)) {
                loadDailyGoals(current,
                    onCallBack = { result ->
                        when (result) {
                            is ApiResult.Error -> {
                                Log.d("loadDailyGoals", "Error: ${result.message}")
                            }

                            is ApiResult.Exception -> {
                                Log.d("loadDailyGoals", "Exception: ${result.error}")
                            }

                            is ApiResult.Fail -> {
                                Log.d("loadDailyGoals", "Fail: ${result.message}")
                            }

                            else -> {}
                        }
                    })
                current = current.plusDays(1)
            }
        }
    }
}