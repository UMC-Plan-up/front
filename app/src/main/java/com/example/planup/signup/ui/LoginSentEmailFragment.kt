package com.example.planup.signup.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.databinding.FragmentLoginSentEmailBinding
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.SignupActivity
import com.example.planup.signup.data.EmailSendRequestDto
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import java.io.EOFException

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

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        /* "이메일을 받지 못하셨나요?" 클릭 시 BottomSheet 표시 */
        binding.tvNotReceivedEmail?.setOnClickListener {
            // popup_resend_email.xml을 사용하는 BottomSheet 호출
            ResendEmailBottomSheet.newInstance(emailArg)
                .show(parentFragmentManager, "ResendEmailBottomSheet")
        }

        emailArg = arguments?.getString("email")
            ?: (requireActivity() as? SignupActivity)?.email.orEmpty()

        /* 이메일 자동 발송 */
        if (emailArg.isNotBlank()) {
            sendVerificationEmail(emailArg)
        } else {
            Log.e("EmailVerify", "이메일 정보가 없습니다.")
        }

        /* [테스트용] 클릭 시 (인증 성공 가정) → 프로필 설정 화면으로 이동
           TODO: 서버에서 인증 완료 후, API를 거쳐 다음 화면(프로필 설정)으로 이동 */
        binding.mockVerifyText.setOnClickListener {
            openProfileSetup()
        }
    }

    /* 이메일 인증 메일 발송 API */
    private fun sendVerificationEmail(email: String) {
        if (isSending) return // 이미 전송 중이면 중복 요청 방지
        isSending = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d("EmailVerify", "인증 메일 발송 요청: email=$email")
                val res = RetrofitInstance.userApi.sendEmail(EmailSendRequestDto(email))

                if (res.isSuccessful) {
                    val body = res.body()
                    if (body?.isSuccess == true) {
                        Log.i("EmailVerify", "발송 성공 message=${body.message}")
                    } else {
                        Log.e("EmailVerify", "발송 실패 code=${body?.code} msg=${body?.message}")
                    }
                } else {
                    val err = res.errorBody()?.string()
                    Log.e("EmailVerify", "HTTP ${res.code()} body=$err")
                }
            } catch (e: EOFException) {
                Log.w("EmailVerify", "EOFException → 발송 성공으로 간주")
            } catch (e: JsonSyntaxException) {
                Log.w("EmailVerify", "JsonSyntaxException → 성공으로 간주")
            } catch (e: Exception) {
                Log.e("EmailVerify", "네트워크 오류: ${e.message}", e)
            } finally {
                isSending = false
            }
        }
    }

    /* ProfileSetupFragment로 이동하는 메서드 */
    private fun openProfileSetup() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.signup_container, ProfileSetupFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}