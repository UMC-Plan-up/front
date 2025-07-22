package com.example.planup.signup.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.signup.SignupActivity

class InviteCodeInputFragment : Fragment(R.layout.fragment_invite_code_input) {

    private lateinit var backIcon: ImageView
    private lateinit var inviteCodeEditText: EditText
    private lateinit var inputButton: AppCompatButton
    private lateinit var textShareLater: TextView

    /* [테스트용] 등록된 초대코드 */
    // TODO : API 연동
    private val validInviteCode = "9ABCDEF273"
    private val friendNickname = "green"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backIcon = view.findViewById(R.id.backIcon)
        inviteCodeEditText = view.findViewById(R.id.nicknameEditText)
        inputButton = view.findViewById(R.id.inputButton)
        textShareLater = view.findViewById(R.id.textShareLater)

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        backIcon.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(InviteCodeFragment())
        }

        hideInvalidCodeMessage()

        /* 초대코드 입력란 클릭 */
        inviteCodeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 입력창 클릭 시
                inviteCodeEditText.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_edittext_focused_blue
                )
                inviteCodeEditText.hint = "" // hint 제거
            } else {
                // 포커스 해제 시
                inviteCodeEditText.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_edittext_rounded
                )
                inviteCodeEditText.hint = "초대코드 입력란" // hint 복원
            }
        }

        /* 입력 버튼 클릭 → 입력된 코드 검증 */
        inputButton.setOnClickListener {
            val enteredCode = inviteCodeEditText.text.toString().trim()

            if (enteredCode.isNotEmpty() && enteredCode == validInviteCode) {
                // 올바른 초대코드 → 팝업 띄우기
                hideInvalidCodeMessage()
                showPopupCenter(view, friendNickname)
            } else if (enteredCode.isNotEmpty()) {
                // 코드 입력했지만 유효하지 않음 → "유효하지 않은 초대코드입니다." 표시
                showInvalidCodeMessage()
            }
            // 입력이 비어있으면 아무 반응 없음
        }

        /* “다음에 할게요” 클릭 → CommunityIntroFragment로 이동 */
        textShareLater.setOnClickListener {
            val communityIntroFragment = CommunityIntroFragment.newInstance(friendNickname)
            (requireActivity() as SignupActivity).navigateToFragment(communityIntroFragment)
        }
    }

    /* 잘못된 코드 text 표시 함수 */
    private fun showInvalidCodeMessage() {
        val invalidText = view?.findViewById<TextView>(R.id.emailFormatErrorText2)
        invalidText?.apply {
            visibility = View.VISIBLE   // 잘못된 코드 메시지 표시

            // 3초 후 자동으로 사라지도록 postDelayed
            postDelayed({
                hideInvalidCodeMessage()
            }, 3000)
        }
    }

    /* 잘못된 코드 text 숨김 함수 */
    private fun hideInvalidCodeMessage() {
        val invalidText = view?.findViewById<TextView>(R.id.emailFormatErrorText2)
        invalidText?.visibility = View.GONE
    }

    /* popup_code.xml을 화면 중앙에 띄우는 함수 */
    private fun showPopupCenter(anchorView: View, nickname: String) {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_code, null)

        // popup_code.xml 안의 친구 닉네임 반영
        val friendDescription = popupView.findViewById<TextView>(R.id.friendDescription)
        friendDescription.text = "$nickname 님과 친구가 되었어요!"

        val confirmButton = popupView.findViewById<AppCompatButton>(R.id.confirmButton)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val popupWidth = (screenWidth * 0.8).toInt()
        val popupHeight = LinearLayout.LayoutParams.WRAP_CONTENT

        val popupWindow = PopupWindow(
            popupView,
            popupWidth,
            popupHeight,
            true
        )

        popupWindow.isOutsideTouchable = false
        popupWindow.isFocusable = true

        /* popup의 확인 버튼 클릭 → CommunityIntroFragment로 이동 */
        confirmButton.setOnClickListener {
            popupWindow.dismiss()

            val communityIntroFragment = CommunityIntroFragment.newInstance(friendNickname)
            (requireActivity() as SignupActivity).navigateToFragment(communityIntroFragment)
        }

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)

        // dim 효과 추가
        val container = popupWindow.contentView.rootView
        val wm = requireActivity().getSystemService(android.content.Context.WINDOW_SERVICE) as android.view.WindowManager
        val p = container.layoutParams as android.view.WindowManager.LayoutParams
        p.flags = p.flags or android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.4f
        wm.updateViewLayout(container, p)
    }
}
