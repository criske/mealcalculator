package com.crskdev.mealcalculator.ui.recipe

import com.crskdev.mealcalculator.platform.router.PlatformBaseRouterWithBack
import com.crskdev.mealcalculator.presentation.common.SourceID
import com.crskdev.mealcalculator.presentation.food.FindFoodRouter
import com.crskdev.mealcalculator.presentation.recipe.RecipeUpsertViewModelRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
class RecipeUpsertViewModelRouterImpl(provider: () -> RecipeUpsertFragment?,
                                      private val findFoodRouter: FindFoodRouter) :
    PlatformBaseRouterWithBack(provider),
    RecipeUpsertViewModelRouter {

    override fun routeToFindFoodNoBackResult() {
        findFoodRouter.routeToFindFoodNoBackResult()
    }

    override fun routeToFindFood(sourceId: SourceID, subSourceId: SourceID) {
        findFoodRouter.routeToFindFood(sourceId, subSourceId)
    }

}