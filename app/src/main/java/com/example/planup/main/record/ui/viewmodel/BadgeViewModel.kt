package com.example.planup.main.record.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.record.adapter.BadgeRow
import com.example.planup.main.record.data.BadgeMapper
import com.example.planup.main.record.ui.repository.BadgeRepository
import com.example.planup.network.onFailWithMessage
import com.example.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BadgeViewModel @Inject constructor(
    private val repository: BadgeRepository
): ViewModel() {
    private val _badges = MutableStateFlow<List<BadgeRow>>(emptyList())
    val badges: StateFlow<List<BadgeRow>> = _badges

    fun loadBadges() {
        viewModelScope.launch {
            repository.loadBadgeList()
                .onSuccess { result ->
                    val unlockedTypes = result.map { it.badgeType }
                    val allBadges = BadgeMapper.createAllBadges()

                    _badges.value = allBadges.map { row ->
                        if (row is BadgeRow.Item) {
                            row.copy(isUnlocked = row.badgeType in unlockedTypes)
                        } else row
                    }
                }.onFailWithMessage {
                    Log.d("loadBadges", "Fail: $it")
                }
        }
    }
}