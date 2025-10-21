package com.example.planup.main.my.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.planup.R
import com.example.planup.component.TopHeader

/**
 * 하위 Route 진입시 공통 화면
 *
 * @param onBack 뒤로가기 액션
 * @param pageContent 페이지 컨텐츠(공통 패딩 처리가 되어있음)
 */
@Composable
fun RoutePageDefault(
    onBack: () -> Unit,
    headerText : String = "목록",
    categoryText : String? = null,
    pageContent : @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopHeader(
            title = headerText,
            onBackAction = onBack
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 20.dp
                )
                .padding(
                    top = 20.dp
                )
        ) {
            categoryText?.let { category ->
                Text(
                    text = category
                )
            }
            pageContent()
        }
    }
}