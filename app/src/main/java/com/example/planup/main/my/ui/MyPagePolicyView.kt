package com.example.planup.main.my.ui

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.planup.R
import com.example.planup.main.my.ui.common.RouteMenuItemWithArrow
import com.example.planup.main.my.ui.common.RoutePageDefault


@Composable
fun MyPagePolicyView(
    onBack: () -> Unit,
    routePolicy: (MyPageRoute.PolicyDetail) -> Unit
) {
    MyPagePolicyContent(
        onBack = onBack,
        routePolicy = routePolicy
    )
}

@Composable
private fun MyPagePolicyContent(
    onBack: () -> Unit,
    routePolicy: (MyPageRoute.PolicyDetail) -> Unit
) {
    RoutePageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.mypage_policy)
    ) {
        Spacer(Modifier.height(20.dp))
        RouteMenuItemWithArrow(
            title = "서비스 이용 약관",
            action = {
                routePolicy(MyPageRoute.PolicyDetail("https://wakeful-dragonfly-7f2.notion.site/242a435fe4bb8032a668f6e4dfd6afc5?pvs=149"))
            }
        )
        RouteMenuItemWithArrow(
            title = "개인정보 처리방침",
            action = {
                routePolicy(MyPageRoute.PolicyDetail("https://wakeful-dragonfly-7f2.notion.site/242a435fe4bb80f18068c43792c0634c?pvs=149"))
            }
        )
    }
}

@Composable
fun MyPagePolicyDetailView(
    onBack: () -> Unit,
    url: String
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            with(settings) {
                domStorageEnabled = true
                javaScriptEnabled = true
            }
            webViewClient = WebViewClient()
        }
    }


    RoutePageDefault(onBack = onBack) {
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxSize(),
            update = { it.loadUrl(url) }
        )
    }
}