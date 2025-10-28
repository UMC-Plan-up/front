package com.example.planup.main.my.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.main.my.ui.common.RouteMenuItem
import com.example.planup.main.my.ui.common.RoutePageDefault

@Composable
fun MyPageOtherView(
    onBack: () -> Unit,
    navigateLogout: () -> Unit,
    navigateDelete: () -> Unit
) {
    RoutePageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.category)
    ) {
        Spacer(Modifier.height(20.dp))
        RouteMenuItem(
            title = stringResource(R.string.mypage_other_logout),
            action = navigateLogout
        )
        RouteMenuItem(
            title = stringResource(R.string.mypage_other_delete),
            action = navigateDelete
        )
    }
}
