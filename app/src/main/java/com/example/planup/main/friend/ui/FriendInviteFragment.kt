package com.example.planup.main.friend.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.planup.R
import com.example.planup.databinding.DropdownFriendInviteBinding
import com.example.planup.databinding.FragmentFriendInviteBinding
import com.example.planup.databinding.ToastCompleteAddFriendBinding
import com.example.planup.main.MainSnackbarViewModel
import com.example.planup.main.friend.ui.viewmodel.FriendViewModel
import com.example.planup.main.user.ui.viewmodel.UserInviteCodeViewModel
import kotlinx.coroutines.launch

class FriendInviteFragment : Fragment() {
    private var _binding: FragmentFriendInviteBinding? = null
    private val binding: FragmentFriendInviteBinding
        get() = _binding!!

    private val friendViewModel: FriendViewModel by activityViewModels()
    private val userInviteCodeViewModel: UserInviteCodeViewModel by activityViewModels()
    private val mainSnackbarViewModel: MainSnackbarViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendInviteBinding.inflate(inflater, container, false)
        return binding.root
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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