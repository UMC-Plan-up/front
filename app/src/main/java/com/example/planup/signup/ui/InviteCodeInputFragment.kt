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
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.SignupActivity
import com.example.planup.signup.data.SignupRepository
import com.example.planup.signup.data.SignupRequestDto
import kotlinx.coroutines.launch

class InviteCodeInputFragment : Fragment(R.layout.fragment_invite_code_input) {

    private lateinit var backIcon: ImageView
    private lateinit var inviteCodeEditText: EditText
    private lateinit var inputButton: AppCompatButton
    private lateinit var textShareLater: TextView

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

            if (enteredCode.isNotEmpty()) {
                // (1) SignupActivity에 값 저장
                val activity = requireActivity() as SignupActivity
                activity.inviteCode = enteredCode
                activity.agreements = listOf(SignupActivity.Agreement(termsId = 1, isAgreed = true))

                // (2) 회원가입 API 요청
                val request = SignupRequestDto(
                    email = activity.email ?: "",
                    password = activity.password ?: "",
                    passwordCheck = activity.password ?: "",
                    nickname = activity.nickname ?: "",
                    inviteCode = enteredCode,
                    profileImg = activity.profileImgUrl ?: "",
                    agreements = activity.agreements!!.map {
                        com.example.planup.signup.data.Agreement(it.termsId, it.isAgreed)
                    }
                )

                lifecycleScope.launch {
                    try {
                        val repository = SignupRepository(RetrofitInstance.signupApi)
                        val response = repository.signup(request)

                        if (response.isSuccess) {
                            // (3) 올바른 초대코드 → 팝업 띄우기
                            hideInvalidCodeMessage()
                            //val nickname = response.result.nickname
                            //showPopupCenter(view, nickname)

                        } else {
                            // (4) 서버 응답 실패 → 코드별 처리
                            handleErrorCode(response.code)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // (5) 네트워크 등 예외 발생 시 처리
                        setErrorMessage("네트워크 오류가 발생했습니다.")
                    }
                }
            }
        }



        /* “다음에 할게요” 클릭 → CommunityIntroFragment로 이동 */
        textShareLater.setOnClickListener {
            val activity = requireActivity() as SignupActivity
            activity.inviteCode = ""
            activity.agreements = listOf(SignupActivity.Agreement(termsId = 1, isAgreed = true))

            val request = SignupRequestDto(
                email = activity.email ?: "",
                password = activity.password ?: "",
                passwordCheck = activity.password ?: "",
                nickname = activity.nickname ?: "",
                inviteCode = "",
                profileImg = activity.profileImgUrl ?: "",
                agreements = activity.agreements!!.map {
                    com.example.planup.signup.data.Agreement(it.termsId, it.isAgreed)
                }
            )

            lifecycleScope.launch {
                try {
                    val repository = SignupRepository(RetrofitInstance.signupApi)
                    val response = repository.signup(request)

                    if (response.isSuccess) {
                        //val nickname = response.result.nickname
                        //val fragment = CommunityIntroFragment.newInstance(nickname)
                        //activity.navigateToFragment(fragment)
                    } else {
                        handleErrorCode(response.code)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    setErrorMessage("네트워크 오류가 발생했습니다.")
                }
            }
        }
    }

    /* 잘못된 코드 text 표시 함수 */
    private fun showInvalidCodeMessage() {
        setErrorMessage("유효하지 않은 초대코드입니다.")
    }

    /* 잘못된 코드 text 숨김 함수 */
    private fun hideInvalidCodeMessage() {
        val invalidText = view?.findViewById<TextView>(R.id.emailFormatErrorText2)
        invalidText?.visibility = View.GONE
    }

    /* 에러 메시지 표시 함수 */
    private fun setErrorMessage(message: String) {
        val errorText = view?.findViewById<TextView>(R.id.emailFormatErrorText2)
        errorText?.text = message
        errorText?.visibility = View.VISIBLE
        errorText?.postDelayed({
            hideInvalidCodeMessage()
        }, 3000)
    }

    /* 응답 코드에 따른 분기 처리 함수 */
    private fun handleErrorCode(code: String) {
        when (code) {
            "S001" -> setErrorMessage("잘못된 입력값입니다.")
            "S002" -> setErrorMessage("서버 에러가 발생했습니다.")
            "U001" -> setErrorMessage("존재하지 않는 사용자입니다.")
        }
    }

    /* popup_code.xml을 화면 중앙에 띄우는 함수 */
    private fun showPopupCenter(anchorView: View, nickname: String) {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_code, null)

        // popup_code.xml 안의 친구 닉네임 반영
        val friendDescription = popupView.findViewById<TextView>(R.id.friendDescription)
        friendDescription.text = getString(R.string.friend_description, nickname)

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
            val fragment = CommunityIntroFragment.newInstance(nickname)
            (requireActivity() as SignupActivity).navigateToFragment(fragment)
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
