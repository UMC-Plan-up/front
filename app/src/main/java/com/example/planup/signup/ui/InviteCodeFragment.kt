package com.example.planup.signup.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.SignupActivity
import kotlinx.coroutines.launch

class InviteCodeFragment : Fragment(R.layout.fragment_invite_code) {

    private lateinit var backIcon: ImageView
    private lateinit var nicknameEditText: EditText
    private lateinit var shareButton: AppCompatButton
    private lateinit var textShareLater: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backIcon = view.findViewById(R.id.backIcon)
        nicknameEditText = view.findViewById(R.id.nicknameEditText)
        shareButton = view.findViewById(R.id.shareButton)
        textShareLater = view.findViewById(R.id.textShareLater)

        // 입력창 클릭 불가 처리
        nicknameEditText.isFocusable = false
        nicknameEditText.isClickable = false
        nicknameEditText.isLongClickable = false

        // 뒤로가기 아이콘 → 이전 화면으로 이동
        backIcon.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(ProfileSetupFragment())
        }

        // 공유 버튼 → popup_share.xml 띄우기
        shareButton.setOnClickListener {
            showSharePopup(it)
        }

        // “다음에 공유할게요” → InviteCodeInputFragment 이동
        textShareLater.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(InviteCodeInputFragment())
        }

        // 서버에서 초대코드 가져오기
        fetchInviteCode()
    }

    // 초대코드를 서버에서 가져와 EditText에 표시
    private fun fetchInviteCode() {
        val token = getAccessToken() ?: return
        val accessToken = "Bearer $token"

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.inviteCodeApi.getInviteCode(accessToken)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val inviteCode = response.body()?.result?.inviteCode ?: ""
                    nicknameEditText.setText(inviteCode)
                } else {
                    Log.e("InviteCode", "API 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("InviteCode", "예외 발생: ${e.message}")
            }
        }
    }

    // SharedPreferences에서 accessToken 불러오기
    private fun getAccessToken(): String? {
        val prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return prefs.getString("accessToken", null)
    }

    // 공유 팝업 보여주기
    private fun showSharePopup(anchorView: View) {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_share, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 8f

        val kakaoShare = popupView.findViewById<TextView>(R.id.kakaoShareText)
        val smsShare = popupView.findViewById<TextView>(R.id.smsShareText)
        val copyText = popupView.findViewById<TextView>(R.id.copyText)
        val etcShare = popupView.findViewById<TextView>(R.id.etcShareText)

        kakaoShare.setOnClickListener {
            // TODO: 카카오톡 공유 기능
            popupWindow.dismiss()
        }
        smsShare.setOnClickListener {
            // TODO: 문자 공유 기능
            popupWindow.dismiss()
        }
        copyText.setOnClickListener {
            // TODO: 코드 복사 기능
            popupWindow.dismiss()
        }
        etcShare.setOnClickListener {
            ShareChannelBottomSheet().show(parentFragmentManager, "ShareChannel")
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(anchorView, 0, dpToPx(7.5f))
    }

    // dp를 픽셀로 변환
    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}