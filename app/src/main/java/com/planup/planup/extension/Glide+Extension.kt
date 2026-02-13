package com.planup.planup.extension

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.planup.planup.R

/**
 * 프로필 설정 확장, 불러오기전 또는 에러 발생시 [R.drawable.profile_image]를 보여준다.
 *
 * @param src 프로필 소스
 */
fun ImageView.loadSafeProfile(src: Any?) {
    Glide.with(this)
        .load(src)
        .placeholder(R.drawable.profile_image)
        .error(R.drawable.profile_image)
        .circleCrop()
        .into(this)
}