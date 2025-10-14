package com.example.planup.main.friend.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.planup.R
import com.example.planup.databinding.BottomShareDialogBinding
import com.example.planup.databinding.DropdownFriendInviteBinding
import com.example.planup.databinding.FragmentFriendInviteBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.user.ui.viewmodel.UserInviteCodeViewModel
import com.example.planup.network.RetrofitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class FriendInviteFragment : Fragment() {
    private var _binding: FragmentFriendInviteBinding? = null
    private val binding: FragmentFriendInviteBinding
        get() = _binding!!

    private val userInviteCodeViewModel : UserInviteCodeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendInviteBinding.inflate(inflater, container, false)
        clickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userInviteCodeViewModel.fetchMyInviteCode { inviteCodeResult ->
            // 필요 시 토스트/로그
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userInviteCodeViewModel.inviteCode.collect {
                    binding.inviteFriendEt.setText(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clickListener() {
        binding.btnBack.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, FriendFragment())
                .commitAllowingStateLoss()
        }

        binding.btnUpload.setOnClickListener {
            showPopupMenu(binding.btnUpload)
        }

        binding.btnSubmitInviteCode.setOnClickListener {
            showCompleteAddFriendToast()
        }
    }

    private fun showCompleteAddFriendToast(){
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.toast_complete_add_friend, binding.root, false)
        val toast = Toast(requireContext())
        toast.view = layout
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 150)
        toast.show()
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

        popupWindow.showAsDropDown(anchor)

        menuBinding.shareKakaoCl.setOnClickListener {
            // TODO: 카카오 공유 로직
            popupWindow.dismiss()
        }

        menuBinding.shareMessageTv.setOnClickListener {
            // TODO: 기본 메시지 공유 로직
            popupWindow.dismiss()
        }

        menuBinding.shareEtcTv.setOnClickListener {
            popupWindow.dismiss()
            showShareBottomSheet()
        }
    }

    /** 하단 공유 BottomSheet (ViewBinding 사용) */
    private fun showShareBottomSheet() {
        val sheetBinding = BottomShareDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(sheetBinding.root)
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        dialog.show()
    }
}