package com.example.planup.onboarding.model

data class TermModel(
    val id: Int,
    val title: String,
    val content: String?,
    val isRequired: Boolean,
    var isChecked: Boolean = false
)