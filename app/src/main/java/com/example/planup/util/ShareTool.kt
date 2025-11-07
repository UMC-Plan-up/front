package com.example.planup.util

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.example.planup.R
import com.example.planup.database.UserInfoSaver
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 공유 관련 기능을 모듈화 해둔 class
 */
@Singleton
class ShareTool @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userInfoSaver: UserInfoSaver
) {
    private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    /**
     * (이름)님이 친구 신청을 보냈어요.
     * Plan-Up에서 함께 목표 달성에 참여해 보세요!
     * (이름)님의 친구 코드 : ######
     */
    private fun makeInviteMessage(code: String): String {
        val name = userInfoSaver.getNickName()
        return context.getString(R.string.friend_code_share, name, code)
    }

    /**
     * 클립보드로 복사합니다.
     */
    fun copyInviteCodeToClipboard(code: String) {
        val clip = ClipData.newPlainText("초대코드", code)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * SMS 문자 전송을 시도합니다.
     */
    fun shareInviteCodeToSMS(code: String) {
        try {
            val uri = "smsto:".toUri()
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", makeInviteMessage(code))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "문자 전송을 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Code 문자열을 전송합니다.
     */
    fun shareText(code: String, title: String = "초대 코드") {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, makeInviteMessage(code))
            type = "text/plain"
        }

        val chooser = Intent.createChooser(sendIntent, title)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "공유할 수 있는 앱이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    //https://developers.kakao.com/tool/template-builder/app/1290033/template/125174
    private val inviteTemplate: Long = 125174

    fun shareToKakao(
        activityContext: Context,
        code: String
    ) {
        val TAG = "JWH"
        if (ShareClient.instance.isKakaoTalkSharingAvailable(activityContext)) {
            // 카카오톡으로 카카오톡 공유 가능
            ShareClient.instance.shareCustom(
                context = context,
                templateId = inviteTemplate,
                templateArgs = mapOf(
                    "NAME" to userInfoSaver.getNickName(),
                    "CODE" to code
                )
            ) { sharingResult, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡 공유 실패", error)
                } else if (sharingResult != null) {
                    Log.d(TAG, "카카오톡 공유 성공 ${sharingResult.intent}")
                    context.startActivity(sharingResult.intent)

                    // 카카오톡 공유에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                    Log.w(TAG, "Warning Msg: ${sharingResult.warningMsg}")
                    Log.w(TAG, "Argument Msg: ${sharingResult.argumentMsg}")
                }
            }
        } else {
            // 카카오톡 미설치: 웹 공유 사용 권장
            // 웹 공유 예시 코드
            val sharerUrl = WebSharerClient.instance.makeCustomUrl(inviteTemplate)

            // CustomTabs으로 웹 브라우저 열기

            // 1. CustomTabsServiceConnection 지원 브라우저 열기
            // ex) Chrome, 삼성 인터넷, FireFox, 웨일 등
            try {
                KakaoCustomTabsClient.openWithDefault(activityContext, sharerUrl)
            } catch (e: UnsupportedOperationException) {
                // CustomTabsServiceConnection 지원 브라우저가 없을 때 예외처리
            }

            // 2. CustomTabsServiceConnection 미지원 브라우저 열기
            // ex) 다음, 네이버 등
            try {
                KakaoCustomTabsClient.open(activityContext, sharerUrl)
            } catch (e: ActivityNotFoundException) {
                // 디바이스에 설치된 인터넷 브라우저가 없을 때 예외처리
            }
        }
    }
}