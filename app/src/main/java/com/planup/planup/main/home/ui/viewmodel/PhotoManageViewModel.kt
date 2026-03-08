package com.planup.planup.main.home.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planup.planup.main.goal.item.GoalPhotoResult
import com.planup.planup.main.home.ui.repository.PhotoManageRepository
import com.planup.planup.network.ApiResult
import com.planup.planup.network.data.TodayTotalTimeResult
import com.planup.planup.network.onFailWithMessage
import com.planup.planup.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoManageViewModel @Inject constructor(
    private val repository: PhotoManageRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    val goalId = savedStateHandle["goalId"] ?: 0
    private val _photoList = MutableStateFlow<List<GoalPhotoResult>>(emptyList())
    val photoList: StateFlow<List<GoalPhotoResult>> = _photoList

    private val _deleteResult = MutableSharedFlow<Unit>()
    val deleteResult = _deleteResult.asSharedFlow()
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    fun setImage(uri: Uri) {
        _imageUri.value = uri
    }

    fun loadPhotos(
        onCallBack: (ApiResult<List<GoalPhotoResult>>) -> Unit
    ) {
        viewModelScope.launch {
            repository.loadPhotos(goalId)
                .onSuccess {
                    _photoList.value = it
                    onCallBack(ApiResult.Success(it))
                }.onFailWithMessage {
                    Log.d("PhotoManageViewModel", "loadPhotos: $it")
                }
        }
    }

    fun deletePhotos(ids: List<Int>) {
        viewModelScope.launch {
            for(photoId in ids){
                repository.deletePhotos(photoId)
                    .onSuccess {
                        Log.d("PhotoManageViewModel", "deletePhotos: $it")
                    }.onFailWithMessage {
                        Log.d("PhotoManageViewModel", "deletePhotos: $it")
                    }
            }
        }
    }

    fun postPhotos(date: String, photoList: List<String>) {
        viewModelScope.launch {
            repository.postPhotos(goalId, date, photoList)
                .onSuccess {
                    Log.d("PhotoManageViewModel", "postPhotos: $it")
                }.onFailWithMessage {
                    Log.d("PhotoManageViewModel", "postPhotos: $it")
                }
        }
    }
}