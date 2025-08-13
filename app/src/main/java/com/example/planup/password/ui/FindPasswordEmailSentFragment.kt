package com.example.planup.password.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.databinding.FragmentFindPasswordEmailSentBinding
import com.example.planup.password.ResetPasswordActivity
import com.example.planup.password.data.PasswordChangeEmailRequestDto
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.ui.ResendEmailBottomsheet
import com.example.planup.signup.ui.ResendMode
import kotlinx.coroutines.launch

class FindPasswordEmailSentFragment : Fragment() {

    private var _binding: FragmentFindPasswordEmailSentBinding? = null
    private val binding get() = _binding!!

    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* FindPasswordFragment에서 넘어올 때 이메일 전달받기 */
        userEmail = arguments?.getString("email")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindPasswordEmailSentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* 전달받은 이메일 적용 */
        userEmail?.let { email ->
            binding.emailSentDescription.text =
                "($email) 인증을 위한 \n링크를 보내드렸어요. \n메일함에서 링크를 확인해 주세요."

            // 화면 진입 시 자동 발송
            sendPasswordChangeEmail(email)
        }

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.resetPasswordContainer, FindPasswordFragment()) // 이전 화면
                .addToBackStack(null)
                .commit()
        }

        /* "이메일을 받지 못하셨나요?" 클릭 → BottomSheet 표시 (PASSWORD 모드) */
        binding.resendInfoText.setOnClickListener {
            userEmail?.let { email ->
                ResendEmailBottomsheet().apply {
                    arguments = bundleOf(
                        "email" to email,
                        "mode"  to ResendMode.PASSWORD
                    )
                }.show(childFragmentManager, "ResendEmailBottomSheet")
            } ?: run {
                Toast.makeText(requireContext(), "이메일 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        /* [테스트용] 클릭 시 (인증 성공 가정) → ResetPasswordFragment로 이동 */
        binding.mockVerifyText.setOnClickListener {
            openResetPasswordStep()
        }
    }

    // 비밀번호 변경 확인 이메일 발송 API (자동 발송용)
    private fun sendPasswordChangeEmail(email: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                RetrofitInstance.userApi
                    .sendPasswordChangeEmail(PasswordChangeEmailRequestDto(email))
            }.onSuccess { res ->
                val body = res.body()
                if (!(res.isSuccessful && body?.isSuccess == true)) {
                    val msg = body?.message ?: "요청 실패"
                    // 등록되지 않은 이메일 케이스 안내 (필요 시 문구 조정)
                    if (msg.contains("존재하지") || msg.contains("없")) {
                        Toast.makeText(requireContext(), "등록되지 않은 이메일입니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                }
                // 성공 시에는 별도 토스트 없이 그대로 유지 (UI는 이미 '보냈습니다' 안내)
            }.onFailure {
                Toast.makeText(requireContext(), it.message ?: "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* ResetPasswordFragment로 이동 */
    private fun openResetPasswordStep() {
        val fragment = ResetPasswordFragment().apply {
            arguments = Bundle().apply {
                putString("email", userEmail) // 이메일 전달
            }
        }
        (requireActivity() as ResetPasswordActivity)
            .navigateToFragment(fragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(email: String): FindPasswordEmailSentFragment {
            val fragment = FindPasswordEmailSentFragment()
            val args = Bundle()
            args.putString("email", email)
            fragment.arguments = args
            return fragment
        }
    }
}
