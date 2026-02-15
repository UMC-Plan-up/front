package com.planup.planup.main.my.ui

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
import com.planup.planup.R
import com.planup.planup.main.my.ui.common.MyPageDefault
import com.planup.planup.main.my.ui.common.RouteMenuItemWithArrow


@Composable
fun MyPagePolicyView(
    onBack: () -> Unit,
    routePolicy: (MyPageRoute.Service.Detail) -> Unit
) {
    MyPagePolicyContent(
        onBack = onBack,
        routePolicy = routePolicy
    )
}

@Composable
private fun MyPagePolicyContent(
    onBack: () -> Unit,
    routePolicy: (MyPageRoute.Service.Detail) -> Unit
) {
    MyPageDefault(
        onBack = onBack,
        categoryText = stringResource(R.string.mypage_policy)
    ) {
        Spacer(Modifier.height(20.dp))
        RouteMenuItemWithArrow(
            title = stringResource(R.string.mypage_policy_service),
            action = {
                routePolicy(
                    MyPageRoute.Service.Detail(
                        "https://wakeful-dragonfly-7f2.notion.site/2a9a435fe4bb80a6957bc53a1c204834?pvs=74"
                    )
                )
            }
        )
        RouteMenuItemWithArrow(
            title = stringResource(R.string.mypage_policy_privacy),
            action = {
                routePolicy(
                    MyPageRoute.Service.Detail(
                        "https://wakeful-dragonfly-7f2.notion.site/2a9a435fe4bb80f783f4c898ca613a0e?pvs=74"
                    )
                )
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


    MyPageDefault(onBack = onBack) {
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxSize(),
            update = { it.loadUrl(url) }
        )
    }
}