package com.planup.planup.main.home.data

import java.time.LocalDate

data class CalendarEvent(
    val goalName: String,
    val period: String,
    val frequency: Int,
    val date: LocalDate
)