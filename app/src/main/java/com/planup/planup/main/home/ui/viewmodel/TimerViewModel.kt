package com.planup.planup.main.home.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planup.planup.main.goal.item.DateMemoResult
import com.planup.planup.main.goal.item.EditGoalResponse
import com.planup.planup.main.goal.item.FriendsTimerResult
import com.planup.planup.main.goal.item.MyGoalListItem
import com.planup.planup.main.goal.item.PostMemoResult
import com.planup.planup.main.home.data.FriendTimer
import com.planup.planup.main.home.ui.repository.TimerRepository
import com.planup.planup.network.ApiResult
import com.planup.planup.network.data.TimerStartResult
import com.planup.planup.network.data.TimerStopResult
import com.planup.planup.network.data.TodayTotalTimeResult
import com.planup.planup.network.onFailWithMessage
import com.planup.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
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

    //private var isRunning = false
    private val _isRunning = MutableStateFlow<Boolean>(false)
    val isRunning: StateFlow<Boolean> = _isRunning
    private var elapsedSeconds = 0
    private var timerId: Int = 0 //0: 타이머 없음

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri
    private var timerJob: Job? = null

    fun setImage(uri: Uri) {
        _imageUri.value = uri
    }

    fun loadGoals(
        onCallBack: (result: ApiResult<List<MyGoalListItem>>) -> Unit
    ) {
        viewModelScope.launch {
            repository.getMyGoalList()
                .onSuccess { result ->
                    _goals.value = result
                    val dummyList: List<MyGoalListItem> = listOf(
                        MyGoalListItem(0,"목표1", "FRIEND", 10, 10,false),
                        MyGoalListItem(-1, "목표2", "FRIEND", 11, 11,false)
                    ) //더미 데이터 << 목표 생성 가능해지면 지우기
                    _goals.value = dummyList
                    onCallBack(ApiResult.Success(result))
                }
            try {
                val result = repository.getMyGoalList()
                if(result is ApiResult.Success) { _goals.value = result.data }
                val dummyList: List<MyGoalListItem> = listOf(
                    MyGoalListItem(0,"목표1", "FRIEND", 10, 10,true),
                    MyGoalListItem(-1, "목표2", "FRIEND", 11, 11,true)
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
        onCallBack: (ApiResult<TodayTotalTimeResult>) -> Unit
    ) {
        viewModelScope.launch {
            repository.getTodayTotalTime(selectedGoalId.value)
                .onSuccess {
                    _timerText.value = it.formattedTime
                }.onFailWithMessage {
                    Log.d("loadTotalTime", "loadTotalTime failed : $it")
                }
        }
    }

    fun selectGoal(goalId: Int) {
        _selectedGoalId.value = goalId
    }

    fun loadFriends(
        onCallBack: (result: ApiResult<List<FriendsTimerResult>>) -> Unit
    ) {
        viewModelScope.launch {
            repository.getFriendsTimer(selectedGoalId.value)
                .onSuccess {
                    _friends.value = it.map {
                        FriendTimer(it.nickname, it.todayTime, it.profileImg)
                    }
                }.onFailWithMessage {
                    Log.d("loadFriends", "loadFriends failed : $it")
                }
        }
    }

    fun loadGoalInfo(onCallBack: (result: ApiResult<EditGoalResponse>) -> Unit) {
        viewModelScope.launch {
            repository.getEditGoal(selectedGoalId.value)
                .onSuccess { result ->
                    goalFreq = result.frequency
                    goalAmount = result.goalAmount
                }.onFailWithMessage {
                    Log.d("loadGoalInfo", "loadGoalInfo failed : $it")
                }
        }
    }

    fun startTimer(
        onCallBack: (result: ApiResult<TimerStartResult>) -> Unit
    ) {
        viewModelScope.launch {
            repository.startTimer(selectedGoalId.value)
                .onSuccess { result ->
                    _isRunning.value = true
                    elapsedSeconds = result.goalTimeMinutes
                    timerId = result.timerId
                    runTimer()
                }.onFailWithMessage {
                    Log.d("startTimer", "startTimer failed : $it")
                }
        }
    }

    fun runTimer() {
        viewModelScope.launch {
            isRunning.collectLatest { running ->
                if (running) {
                    while (true) {
                        delay(1000L)
                        updateTimerText()
                    }
                }
            }
        }
    }

    fun stopTimer(
        onCallBack: (result: ApiResult<TimerStopResult>) -> Unit
    ) {
        _isRunning.value = false
        viewModelScope.launch {
            repository.stopTimer(timerId)
                .onSuccess {
                    timerId = -1
                }.onFailWithMessage {
                    Log.d("stopTimer", "stopTimer failed : $it")
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

    fun clickTimerButton() {
        if (isRunning.value) stopTimer(createErrorHandler("stopTimer"))
        else startTimer(createErrorHandler("startTimer"))
    }

    fun loadMemo(date: String,
        onCallBack: (result: ApiResult<DateMemoResult>) -> Unit
    ) {
        viewModelScope.launch {
            repository.getDateMemo(selectedGoalId.value, date)
                .onSuccess {
                    _memo.value = it.memo
                }.onFailWithMessage {
                    Log.d("loadMemo", "loadMemo failed : $it")
                }
        }
    }

    fun saveMemo(
        date: String, memo: String,
        onCallBack: (ApiResult<PostMemoResult>) -> Unit
    ) {
        viewModelScope.launch {
            repository.saveMemo(selectedGoalId.value,date,memo)
                .onSuccess {
                    _memo.value = it.memo
                    //TODO: post memo api
                }.onFailWithMessage {
                    Log.d("saveMemo", "saveMemo failed : $it")
                }
        }
    }

    fun nextDay() {
        val currentDate = LocalDate.parse(selectedDate.value)
        val nextDate = currentDate.plusDays(1)
        _selectedDate.value = nextDate.toString()
    }

    fun prevDay() {
        val currentDate = LocalDate.parse(selectedDate.value)
        val prevDate = currentDate.minusDays(1)
        _selectedDate.value = prevDate.toString()
    }
    fun <T> createErrorHandler(
        tag: String,
        onSuccess: ((T) -> Unit)? = null): (ApiResult<T>) -> Unit {
        return { result ->
            when (result) {
                is ApiResult.Success -> onSuccess?.invoke(result.data)
                is ApiResult.Error -> Log.d(tag, "Error: ${result.message}")
                is ApiResult.Exception -> Log.d(tag, "Exception: ${result.error}")
                is ApiResult.Fail -> Log.d(tag, "Fail: ${result.message}")
            }
        }
    }
}

sealed class CameraEvent {
    object ShowCameraPopup : CameraEvent()
    object OpenCamera : CameraEvent()
    object OpenGallery : CameraEvent()
}
