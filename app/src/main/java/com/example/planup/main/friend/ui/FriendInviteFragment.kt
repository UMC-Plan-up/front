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
import com.example.planup.main.user.ui.viewmodel.UserInviteCodeViewModel
import com.example.planup.network.ApiResult
import kotlinx.coroutines.launch

class FriendInviteFragment : Fragment() {
    private var _binding: FragmentFriendInviteBinding? = null
    private val binding: FragmentFriendInviteBinding
        get() = _binding!!

    private val userInviteCodeViewModel: UserInviteCodeViewModel by activityViewModels()

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
                userInviteCodeViewModel.fetchInvalidateInviteCode(
                    code = etInviteCodeInput.text.toString()
                ) { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            val data = result.data
                            if (data.valid) {
                                val nickName = data.targetUserNickname
                                showCompleteAddFriendToast(nickName)
                                etInviteCodeInput.setText("")
                            } else {
                                showSimpleToast(data.message)
                            }
                        }

                        is ApiResult.Error -> {
                            showSimpleToast(result.message)
                        }

                        is ApiResult.Exception -> {
                            showSimpleToast("알 수 없는 오류 입니다.")
                        }

                        is ApiResult.Fail -> {
                            showSimpleToast(result.message)
                        }
                    }
                }
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


    private fun showCompleteAddFriendToast(
        nickName: String
    ) {
        val completeAddToastBinding =
            ToastCompleteAddFriendBinding.inflate(layoutInflater, binding.root, false)
        completeAddToastBinding.toastChallengeAcceptTv.text =
            getString(R.string.toast_complete_add_friend, nickName)
        val toast = Toast(requireContext())
        toast.view = completeAddToastBinding.root
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 150)
        toast.show()
    }

    private fun showSimpleToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
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