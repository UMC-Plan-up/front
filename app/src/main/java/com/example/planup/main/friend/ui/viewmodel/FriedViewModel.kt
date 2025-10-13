package com.example.planup.main.friend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planup.main.friend.data.FriendInfo
import com.example.planup.main.friend.domain.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FriedViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel() {


    /**
     * 요청한다.
     * 보통 해당 방식 보다는.. sealedClass를 통해 통신을 하면 더 좋음..
     */
    fun getFriendList(
        onSuccess: (data: List<FriendInfo>?) -> Unit,
        onError: (error: Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val friendList = withContext(Dispatchers.IO) {
                    friendRepository.getFriendList()
                }
                onSuccess(friendList)
            } catch (e: CancellationException) {
                //코루틴의 취소 요청은 정상 신호 이므로 잡으면 안됨
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                //그외 에러는 잡아야됨.
                onError(e)
            }
        }
    }
}