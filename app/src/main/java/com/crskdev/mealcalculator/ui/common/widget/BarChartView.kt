@file:Suppress("MemberVisibilityCanBePrivate")

package com.crskdev.mealcalculator.ui.common.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.utils.dpToPx
import kotlin.properties.Delegates

/**
 * Created by Cristian Pela on 03.02.2019.
 */
class BarChartView : View {

    var data: List<ChartData> by Delegates.observable(emptyList()) { _, _, _ ->
        invalidate()
    }

    private val dataHeights = mutableListOf<Float>()

    private var barWidthPx: Float = 0f

    private var barGapPx: Float = 0f

    private val paint = Paint()

    private val barRect = RectF()

    private val barRectRadius = 5f.dpToPx(resources)

    private val wrappedSize = PointF(100f.dpToPx(resources), 100f.dpToPx(resources))

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val barChartTypedArray = context.obtainStyledAttributes(attrs, R.styleable.BarChart)
        val dataId = barChartTypedArray.getResourceId(R.styleable.BarChart_data, -1)
        if (dataId != -1) {
            val entriesTypedArray = resources.obtainTypedArray(dataId)
            var entryTypedArray: TypedArray? = null

            val dataList = mutableListOf<ChartData>()
            for (e in 0 until entriesTypedArray.length()) {
                entryTypedArray = resources.obtainTypedArray(entriesTypedArray.getResourceId(e, -1))
                val color = entryTypedArray.getColor(0, Color.BLACK)
                val percent = entryTypedArray.getFloat(1, 0f)
                dataList.add(ChartData(color, Percent(percent)))
            }
            data = dataList.toList()
            entryTypedArray?.recycle()
            entriesTypedArray.recycle()
        }
        barChartTypedArray.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val measuredSize = PointF(wrappedSize.x, wrappedSize.y).apply {
            x = resolveSize(x.toInt() + paddingStart + paddingEnd, widthMeasureSpec).toFloat()
            y = resolveSize(y.toInt() + paddingTop + paddingBottom, heightMeasureSpec).toFloat()
        }
        val h = measuredSize.y
        dataHeights.apply {
            clear()
            for (d in data) {
                add((1 - d.percent.value) * (h - paddingTop))
            }
        }
        barRect.bottom = h - paddingBottom

        barGapPx = 2f.dpToPx(resources)
        barWidthPx = (measuredSize.x - paddingStart - paddingEnd) / data.size

        setMeasuredDimension(measuredSize.x.toInt(), measuredSize.y.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        dataHeights.forEachIndexed { i, dh ->
            with(barRect) {
                val gap = if (i > 0 || i < dataHeights.size - 1)
                    barGapPx
                else
                    0f
                left = paddingStart + i * (barWidthPx + gap)
                right = left + barWidthPx - gap
                top = paddingTop + dh
            }
            paint.color = data[i].color
            canvas.drawRoundRect(barRect, barRectRadius, barRectRadius, paint)
        }

    }
}