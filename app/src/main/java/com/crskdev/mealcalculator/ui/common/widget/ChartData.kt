package com.crskdev.mealcalculator.ui.common.widget

import androidx.annotation.FloatRange

/**
 * Created by Cristian Pela on 03.02.2019.
 */
data class ChartData(val color: Int, val percent: Percent)

inline class Percent(@FloatRange(from = 0.0, to = 1.0) val value: Float)