package com.example.planup.signup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.signup.SignupActivity

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

        /* 입력창 클릭 불가 처리 */
        nicknameEditText.isFocusable = false
        nicknameEditText.isClickable = false
        nicknameEditText.isLongClickable = false

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        backIcon.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(ProfileSetupFragment())
        }

        /* 공유 버튼 → popup_share.xml 띄우기 */
        shareButton.setOnClickListener {
            showSharePopup(it)
        }

        /* “다음에 공유할게요” → InviteCodeInputFragment 이동 */
        textShareLater.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(InviteCodeInputFragment())
        }
    }

    /* popup_share.xml을 PopupWindow로 띄우는 함수 */
    // TODO : 공유버튼 누르면 자꾸 LoginActivity로 튕김 수정해야함
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

        // 팝업 내부 버튼 클릭 이벤트
        val kakaoShare = popupView.findViewById<LinearLayout>(R.id.kakaoShareText)
        val smsShare = popupView.findViewById<LinearLayout>(R.id.smsShareText)
        val copyText = popupView.findViewById<LinearLayout>(R.id.copyText)
        val etcShare = popupView.findViewById<LinearLayout>(R.id.etcShareText)

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
            // TODO: 기타 공유 기능
            popupWindow.dismiss()
        }

        // 팝업 띄우기 (버튼 아래)
        popupWindow.showAsDropDown(anchorView)
    }
}

