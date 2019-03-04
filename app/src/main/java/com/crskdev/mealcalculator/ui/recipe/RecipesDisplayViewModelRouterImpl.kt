package com.crskdev.mealcalculator.ui.recipe

import com.crskdev.mealcalculator.platform.router.PlatformBaseRouterWithBack
import com.crskdev.mealcalculator.presentation.recipe.RecipeUpsertRouter
import com.crskdev.mealcalculator.presentation.recipe.RecipesDisplayViewModelRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
class RecipesDisplayViewModelRouterImpl(provider: () -> RecipesDisplayFragment?,
                                        private val recipeUpsertRouter: RecipeUpsertRouter) :
    PlatformBaseRouterWithBack(provider), RecipesDisplayViewModelRouter {

    override fun routeToRecipeUpsertNew() {
        recipeUpsertRouter.routeToRecipeUpsertNew()
    }

    override fun routeToRecipeUpsertEdit(recipeId: Long) {
        recipeUpsertRouter.routeToRecipeUpsertEdit(recipeId)
    }

}