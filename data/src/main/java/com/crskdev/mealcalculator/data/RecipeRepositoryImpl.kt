package com.crskdev.mealcalculator.data

import com.crskdev.mealcalculator.data.internal.room.MealCalculatorDatabase
import com.crskdev.mealcalculator.data.internal.runTransactionDelegate
import com.crskdev.mealcalculator.domain.entities.Recipe
import com.crskdev.mealcalculator.domain.entities.RecipeDetailed
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.gateway.RecipeRepository

/**
 * Created by Cristian Pela on 11.02.2019.
 */
class RecipeRepositoryImpl(private val db: MealCalculatorDatabase) : RecipeRepository {

    override fun save(recipe: Recipe): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRecipeById(id: Long): RecipeDetailed? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(recipe: Recipe) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(recipe: Recipe) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateDetailed(recipe: RecipeDetailed) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeRecipeFood(recipeId: Long, food: RecipeFood) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addRecipeFood(recipeId: Long, food: RecipeFood) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateRecipeFood(recipeId: Long, food: RecipeFood) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun observeAll(observer: (List<Recipe>) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun observeRecipe(id: Long, observer: (RecipeDetailed) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun runTransaction(block: RecipeRepository.() -> Unit) =
        runTransactionDelegate(db, block)
}