package com.planup.planup.main.home.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeTimer(
    val goalId: Int,
    val goalName: String
) : Parcelable