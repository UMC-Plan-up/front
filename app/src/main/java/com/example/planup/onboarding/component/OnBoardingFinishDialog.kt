package com.example.planup.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.button.PlanUpButton
import com.example.planup.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingFinishDialog(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    BasicAlertDialog(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 28.dp),
        onDismissRequest = {},
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.signup_finish),
                style = Typography.Medium_L,
                textAlign = TextAlign.Center
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_fireworks),
                contentDescription = null,
                tint = Color.Unspecified
            )
            PlanUpButton(
                modifier = Modifier
                    .width(110.dp),
                title = stringResource(R.string.btn_ok),
                onClick = onClick
            )
        }
    }
}

@Preview
@Composable
private fun FinishModalPreview() {
    OnBoardingFinishDialog(
        onClick = {}
    )
}