package com.planup.planup.signup.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.planup.planup.R
import com.planup.planup.databinding.FragmentLoginSentEmailBinding
import com.planup.planup.network.RetrofitInstance
import com.planup.planup.signup.SignupActivity
import com.planup.planup.signup.data.EmailSendRequestDto
import kotlinx.coroutines.launch

class LoginSentEmailFragment : Fragment() {

    private var _binding: FragmentLoginSentEmailBinding? = null
    private val binding get() = _binding!!

    private var emailArg: String = ""
    private var isSending = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginSentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailArg = arguments?.getString("email")
            ?: (requireActivity() as? SignupActivity)?.email.orEmpty()

        /* 뒤로가기 아이콘 → LoginPasswordFragment로 이동 */
        binding.backIcon.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.signup_container, LoginPasswordFragment())
                .addToBackStack(null)
                .commit()
        }

        /* "이메일을 받지 못하셨나요?" → BottomSheet 띄우기 */
        binding.tvNotReceivedEmail?.setOnClickListener {
            if (emailArg.isBlank()) {
                return@setOnClickListener
            }


        }

        // 자동 발송
        if (emailArg.isNotBlank()) {
            sendVerificationEmail(emailArg)
        }
    }

    /* 이메일 인증 메일 발송 API */
    private fun sendVerificationEmail(email: String) {
        if (isSending) return

        isSending = true
        Log.d("EmailVerify", "인증 메일 발송 요청: email=$email")

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitInstance.userApi.sendEmail(EmailSendRequestDto(email))

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    // 성공 처리
                } else {
                    val errorBody = response.body()
                    Log.e("EmailVerify", "발송 실패: code=${errorBody?.code} msg=${errorBody?.message}")
                }
            } catch (e: Exception) {
                Log.e("EmailVerify", "네트워크 오류 발생: ${e.message}", e)
            } finally {
                isSending = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
