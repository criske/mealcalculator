package com.crskdev.mealcalculator.domain.interactors

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by Cristian Pela on 10.02.2019.
 */
class RecipeFoodsDiffUtilTest {

    @Test
    fun calculate() {
        assertTrue(RecipeFoodsDiffUtil.calculate(emptyList(), emptyList()).isEmpty())
    }
}