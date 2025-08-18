package com.example.planup.signup.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.SignupActivity
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import kotlinx.coroutines.launch
import com.example.planup.databinding.FragmentInviteCodeBinding
import com.example.planup.databinding.PopupShareBinding

class InviteCodeFragment : Fragment() {

    private var _binding: FragmentInviteCodeBinding? = null
    private val binding get() = _binding!!

    private var myInviteCode: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInviteCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nicknameEditText.isFocusable = false
        binding.nicknameEditText.isClickable = false
        binding.nicknameEditText.isLongClickable = false

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(ProfileSetupFragment())
        }

        /* 공유 버튼 → popup_share.xml 띄우기 */
        binding.shareButton.setOnClickListener {
            showSharePopup(it)
        }

        /* “다음에 공유할게요” → InviteCodeInputFragment 이동 */
        binding.nextButton.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(InviteCodeInputFragment())
        }

        // 서버에서 초대코드 가져오기
        fetchInviteCode()
    }

    /* 초대코드를 서버에서 가져와 EditText에 표시 */
    private fun fetchInviteCode() {

        val token = getAccessToken()
        if (token == null) {
            return
        }

        val accessToken = "Bearer $token"

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.userApi.getInviteCode(accessToken)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val inviteCode = response.body()?.result?.inviteCode ?: ""

                    myInviteCode = inviteCode
                    binding.nicknameEditText.setText(inviteCode)
                } else {
                    Log.e("InviteCode", "API 실패: ${response.code()} / ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("InviteCode", "예외 발생: ${e.message}", e)
            }
        }
    }

    private fun getAccessToken(): String? {
        val prefs = requireActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val token = prefs.getString("accessToken", null)
        Log.d("InviteCode", "불러온 accessToken: $token")
        return token
    }

    /* 공유 팝업 보여주기 */
    private fun showSharePopup(anchorView: View) {
        val popupBinding = PopupShareBinding.inflate(LayoutInflater.from(requireContext()))

        val popupWindow = PopupWindow(
            popupBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 8f


        // 카카오톡 공유
        popupBinding.kakaoShareText.setOnClickListener {
            val nickname = (requireActivity() as SignupActivity).nickname
            val inviteCode = myInviteCode

            val feedTemplate = FeedTemplate(
                content = Content(
                    title = "Plan-Up에서 ${nickname}님이 친구가 되고 싶어요!",
                    description = "친구를 맺고 함께 목표를 달성해보세요. 친구 코드: ${inviteCode}",
                    imageUrl = "https://i.postimg.cc/KjCgBcFB/test2.png",
                    link = Link(
                        mobileWebUrl = "https://play.google.com/store/apps/details?id=com.example.planup"
                    )
                ),
                buttons = listOf(
                    Button(
                        title = "친구 초대 수락",
                        link = Link(
                            mobileWebUrl = "https://play.google.com/store/apps/details?id=com.example.planup",
                            androidExecutionParams = mapOf(
                                "action" to "copy_code",
                                "code" to inviteCode
                            )
                        )
                    )
                )
            )

            ShareClient.instance.shareDefault(requireContext(), feedTemplate) { result, error ->
                if (error != null) {
                    Log.e("KakaoShare", "카카오톡 공유 실패", error)
                } else if (result != null) {
                    startActivity(result.intent)
                    Log.d("KakaoShare", "카카오톡 공유 성공")
                }
            }

            val popupWindowField = anchorView.rootView.findViewById<View>(android.R.id.content)
            (popupWindowField?.parent as? PopupWindow)?.dismiss() ?: popupWindow.dismiss()
        }

        // 문자 공유
        popupBinding.smsShareText.setOnClickListener {
            val nickname = (requireActivity() as SignupActivity).nickname
            val inviteCode = myInviteCode

            val message = """
                ${nickname}님이 친구 신청을 보냈어요.
                Plan-Up에서 함께 목표 달성에 참여해 보세요!
                ${nickname}님의 친구 코드: ${inviteCode}
            """.trimIndent()

            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:")
                putExtra("sms_body", message)
            }

            try {
                startActivity(smsIntent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "문자 앱을 열 수 없습니다", Toast.LENGTH_SHORT).show()
                Log.e("SMS_SHARE", "문자 공유 실패: ${e.message}")
            }
            popupWindow.dismiss()
        }

        // 복사
        popupBinding.copyText.setOnClickListener {
            val inviteCode = myInviteCode

            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("inviteCode", inviteCode)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(requireContext(), "초대코드가 복사되었습니다", Toast.LENGTH_SHORT).show()

            popupWindow.dismiss()
        }

        // 기타
        popupBinding.etcShareText.setOnClickListener {
            val nickname = (requireActivity() as SignupActivity).nickname
            val inviteCode = myInviteCode

            val shareMessage = """
                ${nickname}님이 친구 신청을 보냈어요.
                Plan-Up에서 함께 목표 달성에 참여해 보세요!
                친구 코드: $inviteCode
            """.trimIndent()

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareMessage)
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

            popupWindow.dismiss()
        }
        popupWindow.showAsDropDown(anchorView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
