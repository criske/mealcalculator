package com.crskdev.mealcalculator.ui.food

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.platform.router.PlatformBaseRouter
import com.crskdev.mealcalculator.presentation.food.UpsertFoodRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
class UpsertFoodRouterImpl(provider: () -> Fragment?) : PlatformBaseRouter(provider),
    UpsertFoodRouter {

    override fun routeToUpsertFoodEdit(foodId: Long) {
        findNavController()?.navigate(
            R.id.upsertFoodFragment, bundleOf(
                "name" to null,
                "id" to foodId
            )
        )
    }

    override fun routeToUpsertFoodNew(withName: String?) {
        findNavController()?.navigate(
            R.id.upsertFoodFragment, bundleOf(
                "name" to withName,
                "id" to 0L
            )
        )
    }
}