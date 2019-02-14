package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by Cristian Pela on 10.02.2019.
 */
class RecipeFoodsDiffUtilTest {

    private val EMPTY_FOOD = Food(
        0, "", null, 0,
        Carbohydrate(0f, 0f, 0f), Fat(0f, 0f, 0f),
        0f, 0f
    )

    private val EMPTY_RECIPE_FOOD = RecipeFood(0, EMPTY_FOOD, 0)

    @Test
    fun `should calculate update, delete and add`() {
        val existent = listOf<RecipeFood>(
            EMPTY_RECIPE_FOOD.copy(
                id = 100,
                food = EMPTY_RECIPE_FOOD.food.copy(id = 1),
                quantity = 10
            ),
            EMPTY_RECIPE_FOOD.copy(
                id = 100,
                food = EMPTY_RECIPE_FOOD.food.copy(id = 2),
                quantity = 10
            )
        )
        val updated = listOf<RecipeFood>(
            EMPTY_RECIPE_FOOD.copy(
                id = 100,
                food = EMPTY_RECIPE_FOOD.food.copy(id = 1),
                quantity = 20
            ),
            EMPTY_RECIPE_FOOD.copy(
                id = 100,
                food = EMPTY_RECIPE_FOOD.food.copy(id = 3),
                quantity = 20
            )
        )
        val calculate = RecipeFoodsDiffUtil
            .calculate(existent, updated)
            .map { it.javaClass.simpleName }
            .sorted()

        assertTrue(calculate.size == 3)
        assertEquals(RecipeFoodsDiffUtil.Result.Add::class.java.simpleName, calculate.first())
        assertEquals(RecipeFoodsDiffUtil.Result.Delete::class.java.simpleName, calculate[1])
        assertEquals(RecipeFoodsDiffUtil.Result.Update::class.java.simpleName, calculate[2])
    }

}