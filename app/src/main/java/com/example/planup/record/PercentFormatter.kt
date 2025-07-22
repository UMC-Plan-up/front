package com.example.planup.record

import com.github.mikephil.charting.formatter.ValueFormatter

class PercentFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()}%"
    }
}