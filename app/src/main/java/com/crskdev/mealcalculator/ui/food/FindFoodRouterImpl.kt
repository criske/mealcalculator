package com.crskdev.mealcalculator.ui.food

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.platform.router.PlatformBaseRouter
import com.crskdev.mealcalculator.presentation.common.SourceID
import com.crskdev.mealcalculator.presentation.food.FindFoodRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
class FindFoodRouterImpl(provider: () -> Fragment?) : PlatformBaseRouter(provider), FindFoodRouter {

    override fun routeToFindFood(sourceId: SourceID, subSourceId: SourceID) {
        findNavController()?.navigate(
            R.id.findFoodFragment, bundleOf(
                "sourceId" to sourceId.value,
                "sourceSubId" to subSourceId.value
            )
        )
    }

    override fun routeToFindFoodNoBackResult() {
        findNavController()?.navigate(
            R.id.findFoodFragment, bundleOf(
                "sourceId" to -1,
                "sourceSubId" to -1
            )
        )
    }

}