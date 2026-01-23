package com.example.planup.deeplink

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Uri Scheme 을 처리하는 Activity
 *
 * Scheme 이 planup 인 경우를 처리한다.
 */
class SchemeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("data ${intent.data}")
        println("\thost: ${intent.data?.host}")
    }
}