package com.example.planup.signup.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.SignupActivity
import com.example.planup.signup.data.*
import kotlinx.coroutines.launch
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
            requireActivity().supportFragmentManager.popBackStack()
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
                hideInvalidCodeMessage()
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
                                // 유효한 코드이면 팝업을 띄우고, 팝업의 확인 버튼이 다음 화면으로 이동
                                showPopupCenter(view, result.targetUserNickname, enteredCode)
                            } else {
                                showInvalidCodeMessage()
                            }

                        } else {
                            setErrorMessage("서버 응답이 올바르지 않습니다.")
                        }
                    }

                } catch (e: Exception) {
                    setErrorMessage("네트워크 오류가 발생했습니다.")
                }
            }
        }

        /* 다음 버튼 클릭 → CommunityIntroFragment로 이동 */
        binding.nextButton.setOnClickListener {
            // 입력된 초대 코드 SignupActivity에 저장
            val enteredCode = binding.nicknameEditText.text.toString().trim()
            (requireActivity() as SignupActivity).inviteCode = enteredCode

            (requireActivity() as SignupActivity).navigateToFragment(CommunityIntroFragment())
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

    /* popup_code.xml을 화면 중앙에 띄우고, 확인 시 다음 화면으로 이동 */
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

        /* popup 확인 버튼 → 초대코드 처리 API 호출 후 다음 화면으로 이동 */
        popupBinding.confirmButton.setOnClickListener {
            popupBinding.confirmButton.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val processRes = RetrofitInstance.userApi.processInviteCode(
                        InviteCodeRequest(inviteCode)
                    )
                    val ok = processRes.isSuccessful && processRes.body()?.isSuccess == true &&
                            (processRes.body()?.result?.success == true)

                    if (ok) {
                        popupWindow.dismiss()
                        (requireActivity() as SignupActivity).inviteCode = inviteCode
                        // 초대코드 처리 성공 후 다음 화면으로 이동
                        (requireActivity() as SignupActivity).navigateToFragment(CommunityIntroFragment())
                    } else {
                        val msg = processRes.body()?.message ?: "초대코드 처리 실패"
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        popupBinding.confirmButton.isEnabled = true
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                    popupBinding.confirmButton.isEnabled = true
                }
            }
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
