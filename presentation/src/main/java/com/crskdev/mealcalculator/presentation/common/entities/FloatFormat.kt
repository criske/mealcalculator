package com.crskdev.mealcalculator.presentation.common.entities

/**
 * Created by Cristian Pela on 19.02.2019.
 */
inline class FloatFormat(val float: Float) {

    companion object {
        val ZERO = FloatFormat(0f)
    }


    operator fun invoke(decimalPlaces: Int = 2) =
        "%.${decimalPlaces}f".format(float)


    override fun toString(): String = float.toString()
}

