package com.example.planup.signup.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.example.planup.signup.data.*
import kotlinx.coroutines.launch

class InviteCodeInputFragment : Fragment(R.layout.fragment_invite_code_input) {

    private lateinit var backIcon: ImageView
    private lateinit var inviteCodeEditText: EditText
    private lateinit var inputButton: AppCompatButton
    private lateinit var nextButton: AppCompatButton
    private var myInviteCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val code = arguments?.getString("inviteCode") // 전달받은 code를 꺼냄
        if (!code.isNullOrBlank()) {
            myInviteCode = code
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backIcon = view.findViewById(R.id.backIcon)
        inviteCodeEditText = view.findViewById(R.id.nicknameEditText)
        inputButton = view.findViewById(R.id.inputButton)
        nextButton = view.findViewById(R.id.nextButton)

        if (myInviteCode.isNotBlank()) {
            inviteCodeEditText.setText(myInviteCode)
        }

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        backIcon.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(InviteCodeFragment())
        }

        hideInvalidCodeMessage()

        /* 초대코드 입력란 클릭 */
        inviteCodeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                inviteCodeEditText.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_edittext_focused_blue
                )
                inviteCodeEditText.hint = ""
            } else {
                inviteCodeEditText.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_edittext_rounded
                )
                inviteCodeEditText.hint = "초대코드 입력란"
            }
        }

        /* 입력 버튼 클릭 → 초대코드 실시간 검증 API 요청 */
        inputButton.setOnClickListener {
            val enteredCode = inviteCodeEditText.text.toString().trim()

            Log.d("InviteCode", "입력한 코드: $enteredCode")

            if (enteredCode.isBlank()) {
                proceedSignup("")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val request = InviteCodeValidateRequest(inviteCode = enteredCode)
                    val response = RetrofitInstance.userApi.validateInviteCode(request)

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.isSuccess == true) {
                            val result = responseBody.result

                            if (result.valid) {
                                hideInvalidCodeMessage()
                                showPopupCenter(view, result.targetUserNickname, enteredCode)
                            } else {
                                showInvalidCodeMessage()
                            }

                        } else {
                            setErrorMessage("서버 응답이 올바르지 않습니다.")
                        }
                    } else {
                        setErrorMessage("서버와의 통신에 실패했습니다.")
                    }

                } catch (e: Exception) {
                    setErrorMessage("네트워크 오류가 발생했습니다.")
                }
            }
        }

        /* 다음 버튼 클릭 → 입력된 초대코드로 회원가입 진행 */
        nextButton.setOnClickListener {
            val code = inviteCodeEditText.text.toString().trim()
            proceedSignup(code)
        }

        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (inviteCodeEditText.isFocused) {
                    inviteCodeEditText.clearFocus()
                    hideKeyboard()
                }
                view.performClick()
            }
            false
        }
    }

    private fun proceedSignup(inviteCode: String) {
        val activity = requireActivity() as SignupActivity
        activity.inviteCode = inviteCode

        val agreements = activity.agreements ?: emptyList()

        val request = SignupRequestDto(
            email = activity.email ?: "",
            password = activity.password ?: "",
            passwordCheck = activity.password ?: "",
            nickname = activity.nickname ?: "",
            inviteCode = inviteCode,
            profileImg = activity.profileImgUrl ?: "",
            agreements = agreements.map {
                Agreement(it.termsId, it.isAgreed)
            }
        )

        lifecycleScope.launch {
            try {
                val repository = SignupRepository(RetrofitInstance.userApi)
                val response = repository.signup(request)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.isSuccess == true) {
                        val fragment =
                            CommunityIntroFragment.newInstance(activity.nickname ?: "")
                        activity.navigateToFragment(fragment)
                    } else {
                        handleErrorCode(responseBody?.code ?: "")
                    }
                } else {
                    Log.d("okhttp","signupApi")
                    setErrorMessage("서버와의 통신에 실패했습니다.")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("okhttp","signupApi")
                setErrorMessage("네트워크 오류가 발생했습니다.")
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

    /* popup_code.xml을 화면 중앙에 띄우고, 확인 시 회원가입 API 호출 */
    private fun showPopupCenter(anchorView: View, nickname: String, inviteCode: String) {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_code, null)

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

        /* popup 확인 버튼 → 회원가입 API 호출 */
        confirmButton.setOnClickListener {
            popupWindow.dismiss()
            proceedSignup(inviteCode)
        }

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)

        val container = popupWindow.contentView.rootView
        val wm =
            requireActivity().getSystemService(android.content.Context.WINDOW_SERVICE) as android.view.WindowManager
        val p = container.layoutParams as android.view.WindowManager.LayoutParams
        p.flags = p.flags or android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.4f
        wm.updateViewLayout(container, p)
    }

    /* 키보드 숨기는 메서드 */
    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
