package com.crskdev.mealcalculator.domain.gateway

import com.crskdev.mealcalculator.domain.entities.Recipe
import com.crskdev.mealcalculator.domain.entities.RecipeDetailed
import com.crskdev.mealcalculator.domain.entities.RecipeFood

/**
 * Created by Cristian Pela on 06.02.2019.
 */
interface RecipeRepository : Transactionable<RecipeRepository> {

    fun save(recipe: Recipe): Long

    fun getRecipeDetailedById(id: Long): RecipeDetailed?

    fun getRecipeById(id: Long): Recipe?

    fun delete(recipe: Recipe)

    fun update(recipe: Recipe)

    fun removeRecipeFood(recipeId: Long, food: RecipeFood)

    fun addRecipeFood(recipeId: Long, food: RecipeFood): Long

    fun updateRecipeFood(recipeId: Long, food: RecipeFood)

    suspend fun observeAll(observer: (List<Recipe>) -> Unit)

    suspend fun observeRecipeDetailed(id: Long, observer: (RecipeDetailed) -> Unit)

}