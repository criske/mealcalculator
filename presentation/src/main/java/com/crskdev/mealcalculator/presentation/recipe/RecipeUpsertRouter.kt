package com.crskdev.mealcalculator.presentation.recipe

/**
 * Created by Cristian Pela on 04.03.2019.
 */
interface RecipeUpsertRouter {

    fun routeToRecipeUpsertNew()

    fun routeToRecipeUpsertEdit(recipeId: Long)

}