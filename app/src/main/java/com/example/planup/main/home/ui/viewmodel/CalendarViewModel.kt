package com.example.planup.main.home.ui.viewmodel

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.goal.item.DailyGoalResult
import com.example.planup.main.goal.item.VerifiedGoal
import com.example.planup.main.home.data.CalendarEvent
import com.example.planup.main.home.ui.CalendarRepository
import com.example.planup.network.ApiResult
import com.example.planup.network.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
) : ViewModel() {
    private val _allEvents = MutableLiveData<List<CalendarEvent>>(emptyList())
    val allEvents: LiveData<List<CalendarEvent>> = _allEvents

    private val _selectedDateEvents = MutableLiveData<List<CalendarEvent>>(emptyList())
    val selectedDateEvents: LiveData<List<CalendarEvent>> = _selectedDateEvents

    private var selectedDate: LocalDate = LocalDate.now()

    private var monthList = mutableListOf<CalendarEvent>()


    fun loadMonthlyGoals(
        startDate: LocalDate,
        onCallBack: (result: ApiResult<DailyGoalResult>) -> Unit
    ) {
        viewModelScope.launch{
            var date = startDate
            val endDate = startDate.plusMonths(1).minusDays(1)

            while (!date.isAfter(endDate)) {
                try {
                    val result = calendarRepository.loadDailyGoal(date.toString())
                    if(result is ApiResult.Success) {
                        val dailyGoals = result.data.verifiedGoals
                        dailyGoals.forEach { goal ->
                            monthList.add(
                                CalendarEvent(
                                    goalName = goal.goalName,
                                    period = goal.period ?: "NULL",
                                    frequency = goal.frequency,
                                    date = date
                                )
                            )
                        }
                    }
                    date = date.plusDays(1)
                } catch (e: CancellationException) {
                } catch (e: Exception) {
                    e.printStackTrace()
                    onCallBack(ApiResult.Exception(e))
                }
            }
            _allEvents.value = monthList
            updateSelectedDateEvents()
        }
    }

    fun selectDate(date: LocalDate) {
        selectedDate = date
        updateSelectedDateEvents()
    }

    private fun updateSelectedDateEvents() {
        val events = _allEvents.value?.filter { it.date == selectedDate } ?: emptyList()
        _selectedDateEvents.value = events
    }

    fun getEventsForDate(date: LocalDate): List<CalendarEvent> {
        return _allEvents.value?.filter { it.date == date } ?: emptyList()
    }
}
