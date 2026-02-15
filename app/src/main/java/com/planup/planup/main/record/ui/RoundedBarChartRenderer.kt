package com.planup.planup.main.record.ui

import android.graphics.Canvas
import android.graphics.Path
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

/** 상단만 둥근 막대를 그리는 Renderer */
class RoundedBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler,
    private val radius: Float = 28f
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val roundPath = Path()

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)

        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = dataSet.barBorderWidth

        val barData: BarData = mChart.barData
        mBarBuffers[index].apply {
            setPhases(mAnimator.phaseX, mAnimator.phaseY)
            setDataSet(index)
            setInverted(mChart.isInverted(dataSet.axisDependency))
            setBarWidth(barData.barWidth)
            feed(dataSet)
        }

        trans.pointValuesToPixel(mBarBuffers[index].buffer)
        val buffer = mBarBuffers[index].buffer

        var j = 0
        while (j < buffer.size) {
            val left = buffer[j]
            val top = buffer[j + 1]
            val right = buffer[j + 2]
            val bottom = buffer[j + 3]
            j += 4

            roundPath.reset()
            roundPath.addRoundRect(
                left, top, right, bottom,
                floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f),
                Path.Direction.CW
            )

            mRenderPaint.color = dataSet.color
            c.drawPath(roundPath, mRenderPaint)
        }
    }
}