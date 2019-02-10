package com.crskdev.mealcalculator.domain.gateway

import com.crskdev.mealcalculator.domain.entities.Recipe
import com.crskdev.mealcalculator.domain.entities.RecipeDetailed
import com.crskdev.mealcalculator.domain.entities.RecipeFood

/**
 * Created by Cristian Pela on 06.02.2019.
 */
interface RecipeRepository : Transactionable<RecipeRepository> {

    fun save(recipe: Recipe): Long

    fun getRecipeById(id: Long): RecipeDetailed?

    fun delete(recipe: Recipe)

    fun update(recipe: Recipe)

    fun updateDetailed(recipe: RecipeDetailed)

    fun removeRecipeFood(recipeId: Long, food: RecipeFood)

    fun addRecipeFood(recipeId: Long, food: RecipeFood)

    fun updateRecipeFood(recipeId: Long, food: RecipeFood)

    suspend fun observeAll(observer: (List<Recipe>) -> Unit)

    suspend fun observeRecipe(id: Long, observer: (RecipeDetailed) -> Unit)

}