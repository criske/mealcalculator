package com.crskdev.mealcalculator.ui.recipe

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.platform.router.PlatformBaseRouter
import com.crskdev.mealcalculator.presentation.common.SourceID
import com.crskdev.mealcalculator.presentation.recipe.RecipeDisplayRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
class RecipeDisplayRouterImpl(provider: () -> Fragment?) : PlatformBaseRouter(provider),
    RecipeDisplayRouter {

    override fun routeToRecipesDisplayNoBackResult() {
        findNavController()?.navigate(
            R.id.recipesDisplayFragment, bundleOf(
                "sourceId" to -1,
                "sourceSubId" to -1
            )
        )
    }

    override fun routeToRecipesDisplay(sourceId: SourceID, subSourceId: SourceID) {
        findNavController()?.navigate(
            R.id.recipesDisplayFragment, bundleOf(
                "sourceId" to sourceId.value,
                "sourceSubId" to subSourceId.value
            )
        )
    }

}