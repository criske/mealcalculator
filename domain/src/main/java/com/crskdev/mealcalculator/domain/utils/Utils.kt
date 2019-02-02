package com.crskdev.mealcalculator.domain.utils

import java.lang.Exception
import kotlin.math.absoluteValue

/**
 * Created by Cristian Pela on 02.02.2019.
 */
inline val Int.absoluteCoercedValue: Int
    get() = coerceIn(
        Int.MIN_VALUE,
        Int.MAX_VALUE
    ).absoluteValue

inline val Double.absoluteCoercedValue: Double
    get() = coerceIn(
        Double.MIN_VALUE,
        Double.MAX_VALUE
    ).absoluteValue

inline val Float.absoluteCoercedValue: Float
    get() = coerceIn(
        Float.MIN_VALUE,
        Float.MAX_VALUE
    ).absoluteValue

