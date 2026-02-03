package com.example.planup.signup.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.planup.databinding.PopupResendEmailBinding
import com.example.planup.network.RetrofitInstance
import com.example.planup.password.data.PasswordChangeEmailRequestDto
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import com.example.planup.signup.data.KakaoLoginRequest
import com.example.planup.App
import com.example.planup.util.KakaoServiceHandler

class ResendEmailBottomsheet : BottomSheetDialogFragment() {

    private var _binding: PopupResendEmailBinding? = null
    private val binding get() = _binding!!

    private var isSending = false
    private lateinit var email: String
    private lateinit var mode: ResendMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = arguments?.getString(ARG_EMAIL) ?: ""
        mode  = arguments?.getSerializable(ARG_MODE) as? ResendMode ?: ResendMode.SIGNUP
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PopupResendEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resendEmailOption.setOnClickListener {
            if (!isSending) resendEmail()
        }

        binding.kakaoLoginOption.setOnClickListener {
            if (!isSending) startKakaoLogin()
        }

        binding.cancelButton.setOnClickListener { dismiss() }
    }

    // [카카오톡 소셜 로그인 사용하기] 선택
    private fun startKakaoLogin() {
        isSending = true
        binding.kakaoLoginOption.isEnabled = false
        binding.resendEmailOption.isEnabled = false


        lifecycleScope.launch {
            KakaoServiceHandler.getTokenWithUser(requireActivity())
                .onSuccess { (token, user) ->
                    if(user != null) {
                        callKakaoLogin(token.accessToken, user.kakaoAccount?.email ?: "")
                    }
                }
        }
    }

    private fun callKakaoLogin(accessToken: String, email: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resp = RetrofitInstance.userApi.kakaoLogin(KakaoLoginRequest(accessToken, email))
                val body = resp.body()

                if (resp.isSuccessful && body?.isSuccess == true) {
                    val r = body.result

                    if (r.newUser) {
                        // 신규 유저: SignupActivity로
                        startActivity(
                            Intent(requireContext(), com.example.planup.signup.SignupActivity::class.java).apply {
                                putExtra("provider", "KAKAO")
                                putExtra("tempUserId", r.tempUserId ?: "")
                                putExtra("email", r.userInfo?.email)
                                putExtra("profileImg", r.userInfo?.profileImg)
                            }
                        )
                        dismiss()
                        requireActivity().overridePendingTransition(0, 0)
                    } else {
                        val accessToken = r.accessToken
                        val userInfo = r.userInfo
                        if (!accessToken.isNullOrBlank() && userInfo != null) {
                            App.jwt.token = "Bearer $accessToken"

                            val prefs = requireActivity().getSharedPreferences("userInfo", android.content.Context.MODE_PRIVATE)
                            prefs.edit().apply {
                                putString("accessToken", accessToken)
                                putInt("userId", userInfo.id.toInt())
                                putString("email", userInfo.email)
                                putString("nickname", userInfo.nickname)
                                putString("profileImg", userInfo.profileImg)
                            }.apply()

                            startActivity(Intent(requireContext(), com.example.planup.main.MainActivity::class.java))
                            dismiss()
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "로그인 처리에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            restoreKakaoButtons()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), body?.message ?: "로그인 실패", Toast.LENGTH_LONG).show()
                    restoreKakaoButtons()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                restoreKakaoButtons()
            } finally {
                isSending = false
            }
        }
    }

    private fun restoreKakaoButtons() {
        binding.kakaoLoginOption.isEnabled = true
        binding.resendEmailOption.isEnabled = true
    }

    // [이메일 다시 보내기] 선택
    private fun resendEmail() {
        val targetEmail = email.ifBlank {
            (activity as? com.example.planup.signup.SignupActivity)?.email.orEmpty()
        }

        if (targetEmail.isBlank()) {
            Toast.makeText(requireContext(), "이메일 정보가 없어 재발송할 수 없어요.", Toast.LENGTH_SHORT).show()
            return
        }

        isSending = true
        binding.resendEmailOption.isEnabled = false
        binding.kakaoLoginOption.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                when (mode) {
                    // 회원가입 인증 메일 재발송
                    ResendMode.SIGNUP -> {
                        val res = RetrofitInstance.userApi.sendEmail(
                            com.example.planup.signup.data.EmailSendRequestDto(targetEmail)
                        )
                        val ok = res.isSuccessful && res.body()?.isSuccess == true
                        if (ok) {
                            dismiss()
                        } else {
                            val msg = res.body()?.message ?: res.errorBody()?.string() ?: "재발송 실패"
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            restoreButtons()
                        }
                    }

                    // 비밀번호 변경 메일 재발송
                    ResendMode.PASSWORD -> {
                        val res = RetrofitInstance.userApi.resendPasswordChangeEmail(
                            PasswordChangeEmailRequestDto(targetEmail)
                        )
                        val ok = res.isSuccessful && res.body()?.isSuccess == true
                        if (ok) {
                            dismiss()
                        } else {
                            val msg = res.body()?.message ?: res.errorBody()?.string() ?: "재발송 실패"
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            restoreButtons()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                restoreButtons()
            } finally {
                isSending = false
            }
        }
    }

    private fun restoreButtons() {
        binding.resendEmailOption.isEnabled = true
        binding.kakaoLoginOption.isEnabled = true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_EMAIL = "email"
        private const val ARG_MODE  = "mode"

        fun newInstance(email: String, mode: ResendMode = ResendMode.SIGNUP): com.example.planup.signup.ui.ResendEmailBottomsheet {
            return ResendEmailBottomsheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_EMAIL, email)
                    putSerializable(ARG_MODE, mode)
                }
            }
        }
    }
}
