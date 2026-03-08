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
            reason: String
        ): LoginSuspendDialog {
            return LoginSuspendDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_TIMESTAMP, timeStamp)
                    putInt(KEY_COUNT, count)
                    putString(KEY_STATUS, status)
                    putString(KEY_REASON, reason)
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
        val count = arguments?.getInt(KEY_COUNT) ?: 0
        val status = arguments?.getString(KEY_STATUS).let {
            if (it == "SUSPENDED") {
                "계정 비활성화"
            } else if (it == "DELETED") {
                "계정 삭제"
            }
            "계정 정지"
        }
        val reason = arguments?.getString(KEY_REASON) ?: ""

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return ComposeView(requireContext()).apply {
            setContent {
                SuspendedDialog(
                    timeStamp = timeStamp,
                    count = count,
                    status = status,
                    reason = reason,
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
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    val timeStamp = stringResource(R.string.login_suspend_date, timeStamp)
    val count = stringResource(R.string.login_suspend_report_count, count)
    val status = stringResource(R.string.login_suspend_status, status)
    val reason = stringResource(R.string.login_suspend_reason, reason)

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