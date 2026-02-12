// RoundedCombinedRenderer.kt
package com.planup.planup.main.record.ui

import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.renderer.CombinedChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedCombinedRenderer(
    chart: CombinedChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler,
    private val radius: Float = 28f
) : CombinedChartRenderer(chart, animator, viewPortHandler) {

    override fun createRenderers() {
        // 원래 렌더러 구성
        super.createRenderers()

        // 🔧 WeakReference -> 실제 Chart 인스턴스
        val chart = mChart.get() ?: return

        // mRenderers 중 BarChartRenderer만 교체
        for (i in 0 until mRenderers.size) {
            val r = mRenderers[i]
            if (r is BarChartRenderer) {
                mRenderers[i] = RoundedBarChartRenderer(
                    chart as BarDataProvider,           // ✅ BarDataProvider 전달 (WeakReference 아님)
                    mAnimator,
                    mViewPortHandler,
                    radius
                )
            }
        }
    }
}