package com.crskdev.mealcalculator.data.internal.room

import com.crskdev.mealcalculator.data.internal.room.entities.RecipeFoodDb
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Created by Cristian Pela on 11.02.2019.
 */
internal class RecipeDaoTest : BaseDbTest() {

    private val recipeDao by lazy { db.recipeDao() }
    private val foodDao by lazy { db.foodDao() }

    private var idRecipe1: Long = -1
    private var idRecipe2: Long = -1

    override fun setUp() {
        super.setUp()
        val (idFood1, idFood2) = foodDao.insert(
            Utils.EMPTY_FOOD.copy(name = "food1"),
            Utils.EMPTY_FOOD.copy(name = "food2")
        )
        recipeDao.insertRecipes(
            Utils.EMPTY_RECIPE.copy(name = "recipe1"),
            Utils.EMPTY_RECIPE.copy(name = "recipe2")
        ).also {
            idRecipe1 = it[0]
            idRecipe2 = it[1]
        }

        recipeDao.insertRecipeFood(RecipeFoodDb(0, idRecipe1, idFood1, 10))
        recipeDao.insertRecipeFood(RecipeFoodDb(0, idRecipe1, idFood2, 5))

    }

    @Test
    fun should_recipe1_have_foods() {
        val recipeEntries = recipeDao.getRecipeDetailedById(idRecipe1)
        assertEquals(2, recipeEntries.size)
        recipeEntries[0].also {
            assertEquals("food1", it.food!!.name)
            assertEquals(10, it.quantity)
        }
        recipeEntries[1].also {
            assertEquals("food2", it.food!!.name)
            assertEquals(5, it.quantity)
        }
    }

    @Test
    fun should_recipe2_have_foods_not() {
        val recipeEntries = recipeDao.getRecipeDetailedById(idRecipe2)
        assertEquals(1, recipeEntries.size)
        with(recipeEntries.first()) {
            assertNull(food)
            assertNull(quantity)
            assertEquals(0, id)
        }
    }

}