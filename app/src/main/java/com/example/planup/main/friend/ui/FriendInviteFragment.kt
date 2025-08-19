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
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.databinding.BottomShareDialogBinding
import com.example.planup.databinding.DropdownFriendInviteBinding
import com.example.planup.databinding.FragmentFriendInviteBinding
import com.example.planup.main.MainActivity
import com.example.planup.network.RetrofitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class FriendInviteFragment : Fragment() {
    lateinit var binding: FragmentFriendInviteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendInviteBinding.inflate(inflater, container, false)
        clickListener()
        fetchMyInviteCode()   // ⬅️ 여기서 내 초대코드 자동 로드
        return binding.root
    }

    /** userInfo SharedPreferences 에서 액세스 토큰 읽기 */
    private fun getAccessToken(): String? {
        val prefs = requireActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        return prefs.getString("accessToken", null)
    }

    /** 서버에서 내 초대코드를 받아 EditText 에 채우기 */
    private fun fetchMyInviteCode() {
        val token = getAccessToken() ?: return
        val auth = if (token.startsWith("Bearer ", true)) token else "Bearer $token"

        lifecycleScope.launch {
            try {
                val resp = RetrofitInstance.userApi.getInviteCode(auth)
                if (resp.isSuccessful && resp.body()?.isSuccess == true) {
                    val inviteCode = resp.body()?.result?.inviteCode.orEmpty()
                    // ▼ EditText id 를 실제 레이아웃 id 로 교체하세요
                    binding.inviteFriendEt.setText(inviteCode)
                } else {
                    // 필요 시 토스트/로그
                }
            } catch (e: Exception) {
                // 필요 시 토스트/로그
            }
        }
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