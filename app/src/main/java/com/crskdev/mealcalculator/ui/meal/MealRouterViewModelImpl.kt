package com.crskdev.mealcalculator.ui.meal

import com.crskdev.mealcalculator.platform.router.PlatformBaseRouterWithBack
import com.crskdev.mealcalculator.presentation.common.SourceID
import com.crskdev.mealcalculator.presentation.food.FindFoodRouter
import com.crskdev.mealcalculator.presentation.meal.MealViewModelRouter
import com.crskdev.mealcalculator.presentation.recipe.RecipeDisplayRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
class MealRouterViewModelImpl(provider: () -> MealFragment?,
                              private val findFoodRouter: FindFoodRouter,
                              private val recipeDisplayRouter: RecipeDisplayRouter) :
    PlatformBaseRouterWithBack(provider),
    MealViewModelRouter {

    override fun routeToFindFoodNoBackResult() {
        findFoodRouter.routeToFindFoodNoBackResult()
    }

    override fun routeToFindFood(sourceId: SourceID, subSourceId: SourceID) {
        findFoodRouter.routeToFindFood(sourceId, subSourceId)
    }

    override fun routeToRecipesDisplay(sourceId: SourceID, subSourceId: SourceID) {
        recipeDisplayRouter.routeToRecipesDisplay(sourceId, subSourceId)
    }

    override fun routeToRecipesDisplayNoBackResult() {
        recipeDisplayRouter.routeToRecipesDisplayNoBackResult()
    }

}