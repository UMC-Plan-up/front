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
        val timeStamp = arguments?.getString(KEY_REASON) ?: ""
        val count = arguments?.getInt(KEY_COUNT) ?: 0
        val status = arguments?.getString(KEY_STATUS) ?: ""
        val reason = arguments?.getString(KEY_REASON) ?: ""

        return ComposeView(requireContext()).apply {
            setContent {
                SuspendedDialog(
                    timeStamp = timeStamp,
                    count = count,
                    status = status,
                    reason = reason,
                    onDismiss = {}
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
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "서비스 이용 제한 안내",
                style = Typography.Medium_L,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                modifier = Modifier,
                text = stringResource(R.string.login_suspend_date, timeStamp),
                style = Typography.Medium_SM
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                modifier = Modifier,
                text = stringResource(R.string.login_suspend_report_count, count),
                style = Typography.Medium_SM
            )
            Text(
                modifier = Modifier,
                text = stringResource(R.string.login_suspend_status, status),
                style = Typography.Medium_SM
            )
            Text(
                modifier = Modifier,
                text = stringResource(R.string.login_suspend_reason, reason),
                style = Typography.Medium_SM
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                modifier = Modifier,
                text = stringResource(R.string.login_suspend_contact),
                style = Typography.Medium_SM
            )

            PlanUpButton(
                modifier = Modifier.height(34.dp),
                title = stringResource(R.string.btn_ok)
            ) {
                onDismiss()
            }
        }
    }
}