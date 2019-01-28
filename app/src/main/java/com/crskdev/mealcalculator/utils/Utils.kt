package com.crskdev.mealcalculator.utils

import android.content.res.Resources
import android.util.TypedValue

/**
 * Created by Cristian Pela on 26.01.2019.
 */
fun Float.dpToPx(resources: Resources): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics)


fun Int.dpToPx(resources: Resources): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics)
