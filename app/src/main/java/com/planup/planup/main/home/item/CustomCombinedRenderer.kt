package com.planup.planup.main.home.item

import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.renderer.CombinedChartRenderer
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class CustomCombinedChartRenderer(
    chart: CombinedChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : CombinedChartRenderer(chart, animator, viewPortHandler) {

    init {
        mRenderers = listOf(
            RoundedBarChartRenderer(chart, animator, viewPortHandler), // Bar
            LineChartRenderer(chart, animator, viewPortHandler)         // Line
        )
    }
}
