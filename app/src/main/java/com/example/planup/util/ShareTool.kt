package com.example.planup.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

/**
 * 공유 관련 기능을 모듈화 해둔 class
 */
@Singleton
class ShareTool @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

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
                putExtra("sms_body", code)
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
    fun shareText(text: String, title: String = "초대 코드") {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
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
}