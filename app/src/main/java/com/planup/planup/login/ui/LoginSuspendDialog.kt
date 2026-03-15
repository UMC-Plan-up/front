package com.planup.planup.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import com.planup.planup.R
import com.planup.planup.component.button.PlanUpButton
import com.planup.planup.theme.Typography
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LoginSuspendDialog : DialogFragment() {

    companion object {
        private const val KEY_TIMESTAMP = "key_timestamp"
        private const val KEY_COUNT = "key_count"
        private const val KEY_STATUS = "key_status"
        private const val KEY_REASON = "key_reason"
        private const val KEY_DETAIL_REASON = "key_detail_reason"

        /**
         * @param timeStamp 제제된 날짜
         * @param count 누적 신고 수
         * @param status 제제 상태 (계정 비활성화, 계정 삭제)
         * @param reason 제제 사유
         * @return
         */
        fun newInstance(
            timeStamp: String,
            count: Int,
            status: String,
            reason: String,
            detailReason: String,
        ): LoginSuspendDialog {
            return LoginSuspendDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_TIMESTAMP, timeStamp)
                    putInt(KEY_COUNT, count)
                    putString(KEY_STATUS, status)
                    putString(KEY_REASON, reason)
                    putString(KEY_DETAIL_REASON, detailReason)
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val timeStamp = arguments?.getString(KEY_TIMESTAMP)?.let {
            runCatching {
                LocalDate.parse(it, DateTimeFormatter.ISO_DATE_TIME).format(
                    DateTimeFormatter.ofPattern(
                        "yyyy년 MM월 dd일"
                    )
                )
            }.getOrNull()
        } ?: "xxxx년 xx월 xx일"

        val count = arguments?.getInt(KEY_COUNT) ?: -1

        val status = arguments?.getString(KEY_STATUS).let {
            when (it) {
                "SUSPENDED" -> getString(R.string.suspend_status_suspended)
                "DELETED" -> getString(R.string.suspend_status_deleted)
                else -> getString(R.string.suspend_status_other)
            }
        }

        val reason = arguments?.getString(KEY_REASON)?.let {
            // TODO:: enum 값에 따라 분기
            when(it) {
                "" -> ""
                else -> ""
            }
        } ?: ""

        val detailReason = arguments?.getString(KEY_DETAIL_REASON).let {
            when (it) {
                "ABUSE_OR_HATE_SPEECH" -> getString(R.string.suspend_reason_abuse_or_hate_speech)
                "SEXUAL_CONTENT" -> getString(R.string.suspend_reason_sexual_content)
                "SPAM_OR_ADVERTISING" -> getString(R.string.suspend_reason_span_or_advertising)
                "INAPPROPRIATE_CONTENT" -> getString(R.string.suspend_reason_inappropriate_content)
                "FRAUD_OR_IMPERSONATION" -> getString(R.string.suspend_reason_fraud_or_impersonation)
                "OTHER" -> getString(R.string.suspend_reason_other)
                else -> getString(R.string.suspend_reason_other)
            }
        }


        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return ComposeView(requireContext()).apply {
            setContent {
                SuspendedDialog(
                    timeStamp = timeStamp,
                    count = count,
                    status = status,
                    reason = reason,
                    detailReason = detailReason,
                    onDismiss = {
                        dismiss()
                    }
                )
            }
        }
    }
}


@Composable
private fun SuspendedDialog(
    timeStamp: String,
    count: Int,
    status: String,
    reason: String,
    detailReason: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    val timeStamp = stringResource(R.string.login_suspend_date, timeStamp)
    val count = stringResource(R.string.login_suspend_report_count, count)
    val status = stringResource(R.string.login_suspend_status, status)
    val reason = stringResource(R.string.login_suspend_reason, "$reason $detailReason")

    val body = remember {
        timeStamp + "\n\n" +
                count + "\n" +
                status + "\n" +
                reason
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.login_suspend_title),
                style = Typography.Semibold_L,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = body,
                style = Typography.Medium_SM
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.login_suspend_contact),
                style = Typography.Medium_SM
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PlanUpButton(
                    modifier = Modifier.height(34.dp),
                    title = stringResource(R.string.btn_ok)
                ) {
                    onDismiss()
                }
            }
        }
    }
}