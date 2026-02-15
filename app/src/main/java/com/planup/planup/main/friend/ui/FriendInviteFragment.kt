package com.planup.planup.main.friend.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.planup.planup.R
import com.planup.planup.databinding.DropdownFriendInviteBinding
import com.planup.planup.databinding.FragmentFriendInviteBinding
import com.planup.planup.main.friend.ui.common.FriendDepth2Fragment
import com.planup.planup.main.user.ui.viewmodel.UserInviteCodeViewModel
import kotlinx.coroutines.launch

class FriendInviteFragment : FriendDepth2Fragment<FragmentFriendInviteBinding>(
    FragmentFriendInviteBinding::inflate
) {
    private val userInviteCodeViewModel: UserInviteCodeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userInviteCodeViewModel.fetchMyInviteCode { inviteCodeResult ->
            // 필요 시 토스트/로그
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userInviteCodeViewModel.inviteCode.collect { code ->
                    binding.inviteFriendEt.setText(code)
                    binding.btnCopyInviteCode.isEnabled = code.isNotEmpty()
                    binding.btnUpload.isEnabled = code.isNotEmpty()
                }
            }
        }

        with(binding) {
            btnBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            btnCopyInviteCode.setOnClickListener {
                userInviteCodeViewModel.copyToClipboard()
            }

            btnUpload.setOnClickListener {
                showPopupMenu(btnUpload)
            }

            btnSubmitInviteCode.setOnClickListener {
                btnSubmitInviteCode.isEnabled = false
                userInviteCodeViewModel.fetchInvalidateInviteCode(
                    code = etInviteCodeInput.text.toString(),
                    onCallBack = { processResult ->
                        btnSubmitInviteCode.isEnabled = true
                        val nickName = processResult.friendNickname
                        mainSnackbarViewModel.updateSuccessMessage(
                            getString(R.string.toast_complete_add_friend, nickName)
                        )
                        etInviteCodeInput.setText("")
                        friendViewModel.fetchFriendList()
                    },
                    onError = { message ->
                        btnSubmitInviteCode.isEnabled = true
                        mainSnackbarViewModel.updateErrorMessage(message)
                    }
                )
            }

            etInviteCodeInput.addTextChangedListener {
                btnSubmitInviteCode.isEnabled = it.toString().length == 6
            }
        }
    }

    /** 프로필 사진 재설정 드롭다운 메뉴 (ViewBinding 사용) */
    private fun showPopupMenu(anchor: View) {
        val menuBinding = DropdownFriendInviteBinding.inflate(layoutInflater, null, false)

        val popupWindow = PopupWindow(
            menuBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable())
        }

        // anchor의 오른쪽 끝에 맞추기
        menuBinding.root.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val popupWidth = menuBinding.root.measuredWidth
        val anchorWidth = anchor.width

        // x 오프셋 = anchor의 width - popup의 width
        val xOffset = anchorWidth - popupWidth

        popupWindow.showAsDropDown(anchor, xOffset, 0)

        menuBinding.shareKakaoCl.setOnClickListener {
            userInviteCodeViewModel.shareKaKao(
                this@FriendInviteFragment.requireActivity()
            )
            popupWindow.dismiss()
        }

        menuBinding.shareMessageTv.setOnClickListener {
            userInviteCodeViewModel.shareToSMS()
            popupWindow.dismiss()
        }

        menuBinding.shareEtcTv.setOnClickListener {
            userInviteCodeViewModel.shareEtc()
            popupWindow.dismiss()
        }
    }
}