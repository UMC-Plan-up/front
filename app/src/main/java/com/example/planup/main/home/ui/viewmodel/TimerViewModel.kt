package com.example.planup.main.home.ui.viewmodel

import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.goal.item.DateMemoResult
import com.example.planup.main.goal.item.EditGoalResponse
import com.example.planup.main.goal.item.FriendsTimerResult
import com.example.planup.main.goal.item.MyGoalListItem
import com.example.planup.main.goal.item.PostMemoResult
import com.example.planup.main.home.data.FriendTimer
import com.example.planup.main.home.data.HomeTimer
import com.example.planup.main.home.data.TimerRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.data.TimerStartResult
import com.example.planup.network.data.TimerStopResult
import com.example.planup.network.data.TodayTotalTimeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val repository: TimerRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _goals = MutableStateFlow<List<MyGoalListItem>>(emptyList())
    val goals: StateFlow<List<MyGoalListItem>> = _goals

    private val _friends = MutableStateFlow<List<FriendTimer>>(emptyList())
    val friends: StateFlow<List<FriendTimer>> = _friends

    private val _timerText = MutableStateFlow("00:00:00")
    val timerText: StateFlow<String> = _timerText

    private val _memo = MutableStateFlow("")
    val memo: StateFlow<String> = _memo

    private val _selectedGoalId = MutableStateFlow(0)
    val selectedGoalId: StateFlow<Int> = _selectedGoalId
    var goalFreq = 0
    var goalAmount = "-"
    var preselectedDate: String = savedStateHandle["selectedDate"] ?: LocalDate.now().toString()

    private val _selectedDate = MutableStateFlow(preselectedDate)
    val selectedDate: StateFlow<String> = _selectedDate

    private var isRunning = false
    private var elapsedSeconds = 0
    private var timerId: Int = 0 //0: 타이머 없음

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    fun setImage(uri: Uri) {
        _imageUri.value = uri
    }

    fun loadGoals(
        onCallBack: (result: ApiResult<List<MyGoalListItem>>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = repository.getMyGoalList()
                if(result is ApiResult.Success) { _goals.value = result.data }
                val dummyList: List<MyGoalListItem> = listOf(
                    MyGoalListItem(0,"목표1", "FRIEND", 10, 10),
                    MyGoalListItem(-1, "목표2", "FRIEND", 11, 11)
                ) //더미 데이터 << 목표 생성 가능해지면 지우기
                _goals.value = dummyList
                onCallBack(result)
            } catch (e: CancellationException) {

            } catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }
        }
    }

    fun loadTotalTime(
        goalId: Int,
        onCallBack: (ApiResult<TodayTotalTimeResult>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = repository.getTodayTotalTime(goalId)
                if (result is ApiResult.Success) {
                    _timerText.value = result.data.formattedTime
                }
                onCallBack(result)
            } catch (e: CancellationException) {

            } catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }
        }
    }

    fun selectGoal(goalId: Int) {
        _selectedGoalId.value = goalId
    }

    fun loadFriends(
        goalId: Int,
        onCallBack: (result: ApiResult<List<FriendsTimerResult>>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = repository.getFriendsTimer(goalId)
                if (result is ApiResult.Success) {
                    _friends.value = result.data.map {
                        FriendTimer(it.nickname, it.todayTime, it.profileImg)
                    }
                }
                onCallBack(result)
            } catch (e: CancellationException) {

            } catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }
        }
    }

    fun loadGoalInfo(goalId: Int, onCallBack: (result: ApiResult<EditGoalResponse>) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.getEditGoal(goalId)
                if (result is ApiResult.Success) {
                    goalFreq = result.data.frequency
                    goalAmount = result.data.goalAmount
                }
                onCallBack(result)
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }
        }
    }

    fun startTimer(
        goalId: Int,
        onCallBack: (result: ApiResult<TimerStartResult>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = repository.startTimer(goalId)
                if (result is ApiResult.Success) {
                    isRunning = true
                    elapsedSeconds = result.data.goalTimeMinutes
                    timerId = result.data.timerId
                }
                onCallBack(result)
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }
        }
    }

    fun stopTimer(
        onCallBack: (result: ApiResult<TimerStopResult>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = repository.stopTimer(timerId)
                if (result is ApiResult.Success) {
                    isRunning = false
                    timerId = 0
                }
                onCallBack(result)
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }
        }
    }

    fun updateTimerText() {
        elapsedSeconds++
        val hours = elapsedSeconds / 3600
        val minutes = (elapsedSeconds % 3600) / 60
        val seconds = elapsedSeconds % 60
        _timerText.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun clickTimerButton(goalId: Int) {
        if (isRunning) stopTimer(
            onCallBack = { result ->
                when (result) {
                    is ApiResult.Error -> { Log.d("stopTimer", "Error: ${result.message}") }
                    is ApiResult.Exception -> { Log.d("stopTimer", "Exception: ${result.error}") }
                    is ApiResult.Fail -> { Log.d("stopTimer", "Fail: ${result.message}") }
                    else -> {}
                }
            }
        )
        else startTimer(
            goalId,
            onCallBack = { result ->
                when (result) {
                    is ApiResult.Error -> { Log.d("startTimer", "Error: ${result.message}") }
                    is ApiResult.Exception -> { Log.d("startTimer", "Exception: ${result.error}") }
                    is ApiResult.Fail -> { Log.d("startTimer", "Fail: ${result.message}") }
                    else -> {}
                }
            }
        )
    }

    fun loadMemo(
        goalId: Int, date: String,
        onCallBack: (result: ApiResult<DateMemoResult>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.getDateMemo(goalId, date)
                if (response is ApiResult.Success) _memo.value = response.data.memo
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }
        }
    }

    fun saveMemo(
        goalId: Int, date: String, memo: String,
        onCallBack: (ApiResult<PostMemoResult>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.saveMemo(goalId, date, memo)
                if (response is ApiResult.Success) {

                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                e.printStackTrace()
                onCallBack(ApiResult.Exception(e))
            }
        }
    }
}

sealed class CameraEvent {
    object ShowCameraPopup : CameraEvent()
    object OpenCamera : CameraEvent()
    object OpenGallery : CameraEvent()
}
