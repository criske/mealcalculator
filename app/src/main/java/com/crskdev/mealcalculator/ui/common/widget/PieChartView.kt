package com.crskdev.mealcalculator.ui.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Created by Cristian Pela on 03.02.2019.
 */
class PieChartView : View {

    private val colors = arrayOf(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW)

    private val proportions = arrayOf(0.5f, 0.3f, 0.1f, 0.1f).apply {
        assert(sum() == 1.0f)
    }

    private val bounds by lazy {
        RectF(0f + paddingStart,
            0f + paddingTop,
            width.toFloat() - paddingEnd,
            height.toFloat() - paddingBottom)
    }

    private val paint = Paint()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

    }


    override fun onDraw(canvas: Canvas) {
        var angle = -90f
        for (c in 0 until colors.size) {
            val sweepAngle = 360 * proportions[c]
            paint.color = colors[c]
            canvas.drawArc(bounds, angle, sweepAngle, true, paint)
            angle += sweepAngle
        }
    }
}