package com.planup.planup.deeplink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import com.planup.planup.main.MainActivity

/**
 * Uri Scheme 을 처리하는 Activity
 *
 * Scheme 이 planup 인 경우를 처리한다.
 */
class SchemeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleDeepLink()
    }

    private fun handleDeepLink() {
        val deeplinkUri = intent.data
        Log.d("SchemeActivity", "Received URI: $deeplinkUri")

        val intent: Intent = deeplinkUri?.let {
            DeeplinkInfo(deeplinkUri)?.getIntent(this, it)
        } ?: run {
            // DeeplinkInfo 에 등록하지 않은 딥링크인 경우 메인으로 이동
            Log.e("SchemeActivity", "Deeplink is not match with deeplinkInfo: $deeplinkUri")
            DeeplinkInfo.getMainIntent(this)
        }

        // 앱이 실행중이지 않고, 부모 액티비티를 메인으로 해야하는 경우
        if (isTaskRoot && needMainForParents(deeplinkUri)) {
            TaskStackBuilder.create(this).apply {
                addNextIntentWithParentStack(MainActivity.getIntent(this@SchemeActivity))
                addNextIntent(intent)
            }.startActivities()
        } else {
            startActivity(intent)
        }

        finish()
    }

    private fun needMainForParents(deeplinkUri: Uri?): Boolean {
        return deeplinkUri?.let {
            DeeplinkInfo(deeplinkUri)?.needMainForParents
        } ?: true
    }
}