package com.example.planup.signup.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.EOFException
import com.google.gson.JsonSyntaxException
import com.example.planup.databinding.FragmentInviteCodeInputBinding
import com.example.planup.databinding.PopupCodeBinding

class InviteCodeInputFragment : Fragment() {

    private var _binding: FragmentInviteCodeInputBinding? = null
    private val binding get() = _binding!!

    private var myInviteCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = arguments?.getString("inviteCode")
        if (!code.isNullOrBlank()) {
            myInviteCode = code
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInviteCodeInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (myInviteCode.isNotBlank()) {
            binding.nicknameEditText.setText(myInviteCode)
        }

        hideInvalidCodeMessage()

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(InviteCodeFragment())
        }

        /* 초대코드 입력란 클릭 */
        binding.nicknameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.nicknameEditText.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.bg_edittext_focused_blue
                )
                binding.nicknameEditText.hint = ""
            } else {
                binding.nicknameEditText.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.bg_edittext_rounded
                )
                binding.nicknameEditText.hint = "초대코드 입력란"
            }
        }

        /* 입력 버튼 클릭 → 초대코드 실시간 검증 API 요청 */
        binding.inputButton.setOnClickListener {
            val enteredCode = binding.nicknameEditText.text.toString().trim()

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
        binding.nextButton.setOnClickListener {
            val code = binding.nicknameEditText.text.toString().trim()
            val activity = requireActivity() as SignupActivity
            activity.inviteCode = code
            val fragment = CommunityIntroFragment.newInstance(activity.nickname ?: "")
            activity.navigateToFragment(fragment)
        }


        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (binding.nicknameEditText.isFocused) {
                    binding.nicknameEditText.clearFocus()
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

        // 초대코드 옵션 처리: 비었으면 null → JSON에서 키 자체가 빠지게 함
        val inviteCodeParam: String? = inviteCode.trim().takeIf { it.isNotBlank() }

        val request = SignupRequestDto(
            email = activity.email ?: "",
            password = activity.password ?: "",
            passwordCheck = activity.password ?: "",
            nickname = activity.nickname ?: "",
            inviteCode = inviteCodeParam,
            profileImg = activity.profileImgUrl ?: "",
            agreements = agreements.map { Agreement(it.termsId, it.isAgreed) }
        )

        fun goNext() {
            Log.i("SignupFlow", "회원가입 성공 → 다음 화면 이동")
            val fragment = CommunityIntroFragment.newInstance(activity.nickname ?: "")
            activity.navigateToFragment(fragment)
        }

        lifecycleScope.launch {
            try {
                Log.d("SignupFlow", "요청 JSON=\n${Gson().toJson(request)}")
                Log.d("SignupFlow", "회원가입 API 요청 시작: inviteCode=$inviteCodeParam")

                val repository = SignupRepository(RetrofitInstance.userApi)
                val response = repository.signup(request)

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body == null) {
                        Log.w("SignupFlow", "서버 응답 바디 없음 → 성공 처리")
                        goNext()
                        return@launch
                    }

                    if (body.isSuccess) {
                        Log.i("SignupFlow", "서버 응답 성공 코드 수신")
                        goNext()
                    } else {
                        Log.e("SignupFlow", "서버 응답 실패 code=${body.code} msg=${body.message}")
                        handleErrorCode(body.code ?: "")
                    }
                } else {
                    val err = response.errorBody()?.string()
                    Log.e(
                        "SignupFlow",
                        "HTTP 실패 code=${response.code()} message=${response.message()} body=$err"
                    )
                    setErrorMessage("가입 실패: ${response.code()}")
                }

            } catch (e: EOFException) {
                Log.w("SignupFlow", "EOFException(빈 응답) → 성공 처리")
                goNext()

            } catch (e: JsonSyntaxException) {
                Log.w("SignupFlow", "JsonSyntaxException(예상치 못한 형식) → 성공 처리")
                goNext()

            } catch (e: Exception) {
                Log.e("SignupFlow", "네트워크/알 수 없는 오류: ${e.message}")
                e.printStackTrace()
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
        binding.emailFormatErrorText2.visibility = View.GONE
    }

    /* 에러 메시지 표시 함수 */
    private fun setErrorMessage(message: String) {
        binding.emailFormatErrorText2.text = message
        binding.emailFormatErrorText2.visibility = View.VISIBLE
        binding.emailFormatErrorText2.postDelayed({
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
        val popupBinding = PopupCodeBinding.inflate(LayoutInflater.from(requireContext()))

        popupBinding.friendDescription.text = getString(R.string.friend_description, nickname)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val popupWidth = (screenWidth * 0.8).toInt()
        val popupHeight = LinearLayout.LayoutParams.WRAP_CONTENT

        val popupWindow = PopupWindow(
            popupBinding.root,
            popupWidth,
            popupHeight,
            true
        )

        popupWindow.isOutsideTouchable = false
        popupWindow.isFocusable = true

        /* popup 확인 버튼 → 회원가입 API 호출 */
        popupBinding.confirmButton.setOnClickListener {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
