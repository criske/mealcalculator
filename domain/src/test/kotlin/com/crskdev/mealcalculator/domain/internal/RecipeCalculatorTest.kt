package com.crskdev.mealcalculator.domain.internal

import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.MealEntry
import org.junit.Test

/**
 * Created by Cristian Pela on 24.01.2019.
 */
class RecipeCalculatorTest {

    //https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2771116/
    @Test
    fun calculate() {
        val oatmeal = Food(
            -1,
            "",
            null,
            372,
            Carbohydrate(58.7f, 10f, 0.7f),
            Fat(7f, 1.3f, 5.7f),
            13.5f,
            83
        )


        val apple = Food(
            -1,
            "",
            null,
            52,
            Carbohydrate(14f, 2.4f, 10f),
            Fat(0.2f, 0f, 0.1f),
            0.3f,
            39
        )

        val greekYogurt = Food(
            -1,
            "",
            null,
            59,
            Carbohydrate(3.6f, 0f, 3.2f),
            Fat(0.4f, 0.1f, 0.1f),
            10f,
            12
        )
        val meal = RecipeCalculator().run {
            calculate(
                listOf(
                    MealEntry(-1, -1, "",1,128, oatmeal),
                    MealEntry(-1, -1, "",1,95, apple),
                    MealEntry(-1, -1, "",1,198, greekYogurt)
                )
            )
        }

//        assertEquals(26, meal.calories)
//        with(meal.carbohydrate) {
//            assertEquals(7f, total, 0.01f)
//            assertEquals(1.2f, fiber, 0.01f)
//            assertEquals(5f, sugar, 0.01f)
//        }
       // assertEquals(55.53f, meal.glycemicLoad, 0.01f)

    }
}