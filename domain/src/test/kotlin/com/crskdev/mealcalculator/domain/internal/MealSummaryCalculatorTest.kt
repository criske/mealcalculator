package com.crskdev.mealcalculator.domain.internal

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Cristian Pela on 24.01.2019.
 */
class MealSummaryCalculatorTest {

    @Test
    fun calculate() {

        val meal = MealSummaryCalculator().run {
            calculate(emptyList())
        }

    }
}