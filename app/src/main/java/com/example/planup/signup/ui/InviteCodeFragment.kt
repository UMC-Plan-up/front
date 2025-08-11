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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.SignupActivity
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import kotlinx.coroutines.launch

class InviteCodeFragment : Fragment(R.layout.fragment_invite_code) {

    private lateinit var backIcon: ImageView
    private lateinit var nicknameEditText: EditText
    private lateinit var shareButton: AppCompatButton
    private lateinit var nextButton: TextView

    private var myInviteCode: String = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backIcon = view.findViewById(R.id.backIcon)
        nicknameEditText = view.findViewById(R.id.nicknameEditText)
        shareButton = view.findViewById(R.id.shareButton)
        nextButton = view.findViewById(R.id.nextButton)


        // 입력창 클릭 불가
        nicknameEditText.isFocusable = false
        nicknameEditText.isClickable = false
        nicknameEditText.isLongClickable = false

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        backIcon.setOnClickListener {
            (requireActivity() as SignupActivity).navigateToFragment(ProfileSetupFragment())
        }

        /* 공유 버튼 → popup_share.xml 띄우기 */
        shareButton.setOnClickListener {
            showSharePopup(it)
        }

        /* “다음에 공유할게요” → InviteCodeInputFragment 이동 */
        nextButton.setOnClickListener {
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
                    nicknameEditText.setText(inviteCode)
                } else {
                    Log.e("InviteCode", "API 실패: ${response.code()} / ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("InviteCode", "예외 발생: ${e.message}", e)
            }
        }
    }

    private fun getAccessToken(): String? {
        val prefs = requireActivity().applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("accessToken", null)
        Log.d("InviteCode", "불러온 accessToken: $token")
        return token
    }

    /* 공유 팝업 보여주기 */
    private fun showSharePopup(anchorView: View) {
        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_share, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 8f

        val kakaoShare = popupView.findViewById<TextView>(R.id.kakaoShareText)
        val smsShare = popupView.findViewById<TextView>(R.id.smsShareText)
        val copyText = popupView.findViewById<TextView>(R.id.copyText)
        val etcShare = popupView.findViewById<TextView>(R.id.etcShareText)

        /* TODO : 아직 수정 중 */
        // 카카오톡 공유
        kakaoShare.setOnClickListener {
            val nickname = (requireActivity() as SignupActivity).nickname
            val inviteCode = myInviteCode

            val feedTemplate = FeedTemplate(
                content = Content(
                    title = "Plan-Up에서 ${nickname}님이 친구가 되고 싶어요!",
                    description = "친구를 맺고 함께 목표를 달성해보세요. 친구 코드: ${inviteCode}",
                    imageUrl = "https://i.postimg.cc/fRpYNvqR/planup-kakao.png",
                    link = Link(
                        mobileWebUrl = "https://play.google.com/store/apps/details?id=com.example.planup" // ✅ 콤마 추가
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
        smsShare.setOnClickListener {
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
        copyText.setOnClickListener {
            val inviteCode = myInviteCode

            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("inviteCode", inviteCode)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(requireContext(), "초대코드가 복사되었습니다", Toast.LENGTH_SHORT).show()

            popupWindow.dismiss()
        }

        // 기타
        etcShare.setOnClickListener {
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
    }

    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
