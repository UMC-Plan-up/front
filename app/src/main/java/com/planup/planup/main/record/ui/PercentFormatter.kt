package com.planup.planup.main.record.ui

import com.github.mikephil.charting.formatter.ValueFormatter

class PercentFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()}%"
    }
}