package com.crskdev.mealcalculator.ui.food

import com.crskdev.mealcalculator.platform.router.PlatformBaseRouterWithBack
import com.crskdev.mealcalculator.presentation.food.FindFoodViewModelRouter
import com.crskdev.mealcalculator.presentation.food.UpsertFoodRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
class FindFoodViewModelRouterImpl(
    provider: () -> FindFoodFragment?,
    private val upsertFoodRouter: UpsertFoodRouter) : PlatformBaseRouterWithBack(provider),
    FindFoodViewModelRouter {

    override fun routeToUpsertFoodEdit(foodId: Long) {
        upsertFoodRouter.routeToUpsertFoodEdit(foodId)
    }

    override fun routeToUpsertFoodNew(withName: String?) {
        upsertFoodRouter.routeToUpsertFoodNew(withName)
    }

}