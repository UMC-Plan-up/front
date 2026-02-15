package com.planup.planup.signup.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.planup.planup.R
import com.planup.planup.databinding.FragmentLoginEmailBinding
import com.planup.planup.signup.SignupActivity
import com.planup.planup.network.RetrofitInstance
import com.planup.planup.signup.data.SignUpDraftStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginEmailFragment : Fragment() {

    private var _binding: FragmentLoginEmailBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { RetrofitInstance.userApi }
    private var emailCheckJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailErrorText1.visibility = View.GONE
        binding.emailErrorText2.visibility = View.GONE

        disableNextButton()

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            val agreementFragment = AgreementFragment()
            (activity as? SignupActivity)?.navigateToFragment(agreementFragment)
                ?: requireActivity().supportFragmentManager.popBackStack()
        }

        /* 이메일 입력 변화 감지 → 실시간 검증 */
        binding.emailEditText.addTextChangedListener {
            val email = it.toString().trim()

            // (1) 이메일 형식 확인
            val isValidFormat = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            if (!isValidFormat) {
                showEmailFormatError()  // 형식 에러 표시
                disableNextButton()
                emailCheckJob?.cancel()
                return@addTextChangedListener
            }

            // (2) 이메일 중복 여부 확인
            emailCheckJob?.cancel()
            emailCheckJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(400)
                runCatching { api.checkEmailDuplicate(email) }
                    .onSuccess { res ->
                        if (res.body()?.result?.available == true) {
                            // (3) 정상 입력 → 에러 숨기고 버튼 활성화
                            hideAllErrors()
                            enableNextButton()
                        } else {
                            // 중복 이메일 에러 표시
                            showEmailUsedError()
                            disableNextButton()
                        }
                    }
                    .onFailure {
                        // 네트워크 오류
                        Toast.makeText(
                            requireContext(),
                            "네트워크 오류가 발생했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        showEmailUsedError()
                        disableNextButton()
                    }
            }
        }

        /* 다음 버튼 클릭 → LoginPasswordFragment로 이동 */
        binding.nextButton.setOnClickListener {
            if (binding.nextButton.isEnabled) {  // 활성화 된 경우만 이동 가능
                openNextStep()
            }
        }

        /* EditText 외부 터치 시 키보드 자동 숨김 */
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (binding.emailEditText.isFocused) {
                    binding.emailEditText.clearFocus()
                    hideKeyboard()
                }
                view.performClick()
            }
            false
        }
    }

    /* LoginPasswordFragment로 이동하는 메서드 */
    private fun openNextStep() {
        val email = binding.emailEditText.text.toString().trim()

        // (1) SignupActivity에 email 저장
        val activity = requireActivity() as SignupActivity
        activity.email = email

        SignUpDraftStore.saveEmail(requireContext(), email)

        // (2) LoginPasswordFragment로 이동
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.signup_container, LoginPasswordFragment())
            .addToBackStack(null)
            .commit()
    }

    /* 이메일 형식 에러 표시 */
    private fun showEmailFormatError() {
        binding.emailErrorText1.visibility = View.VISIBLE   // 형식 에러 보여줌
        binding.emailErrorText2.visibility = View.GONE      // 중복 에러는 숨김
    }

    /* 이미 사용 중인 이메일 에러 표시 */
    private fun showEmailUsedError() {
        binding.emailErrorText1.visibility = View.GONE      // 형식 에러 숨김
        binding.emailErrorText2.visibility = View.VISIBLE   // 중복 에러 보여줌
    }

    private fun hideAllErrors() {
        binding.emailErrorText1.visibility = View.GONE
        binding.emailErrorText2.visibility = View.GONE
    }

    /* 다음 버튼 활성 ↔ 비활성 */
    private fun enableNextButton() {  // 다음 버튼 활성화
        binding.nextButton.isEnabled = true
        binding.nextButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.blue_200)
    }

    private fun disableNextButton() {  // 다음 버튼 비활성화
        binding.nextButton.isEnabled = false
        binding.nextButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.black_200)
    }

    /* 키보드 숨기는 메서드 */
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}