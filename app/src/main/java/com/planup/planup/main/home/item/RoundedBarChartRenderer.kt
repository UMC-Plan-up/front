package com.planup.planup.main.home.item

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.RectF
import android.graphics.Shader
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        val barBuffer = mBarBuffers[index]
        barBuffer.setPhases(mAnimator.phaseX, mAnimator.phaseY)
        barBuffer.setDataSet(index)
        barBuffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        barBuffer.setBarWidth((mChart.barData?.barWidth ?: 0.4f))
        barBuffer.feed(dataSet)
        trans.pointValuesToPixel(barBuffer.buffer)

        val radius = 20f

        for (j in barBuffer.buffer.indices step 4) {
            val left = barBuffer.buffer[j]
            val top = barBuffer.buffer[j + 1]
            val right = barBuffer.buffer[j + 2]
            val bottom = barBuffer.buffer[j + 3]

            val rectF = RectF(left, top, right, bottom)

            val gradient = LinearGradient(
                left, 0f, right, 0f,
                Color.parseColor("#A4C2FD"),
                Color.parseColor("#5C91FC"),
                Shader.TileMode.MIRROR
            )
            mRenderPaint.shader = gradient

            c.drawRoundRect(rectF, radius, radius, mRenderPaint)
        }
    }
}
