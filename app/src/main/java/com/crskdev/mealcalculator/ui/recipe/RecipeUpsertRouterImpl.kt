package com.crskdev.mealcalculator.ui.recipe

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.platform.router.PlatformBaseRouter
import com.crskdev.mealcalculator.presentation.recipe.RecipeUpsertRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
class RecipeUpsertRouterImpl(provider: () -> Fragment?) : PlatformBaseRouter(provider),
    RecipeUpsertRouter {

    override fun routeToRecipeUpsertNew() {
        findNavController()?.navigate(
            R.id.recipeUpsertFragment, bundleOf(
                "id" to 0L
            )
        )
    }

    override fun routeToRecipeUpsertEdit(recipeId: Long) {
        findNavController()?.navigate(
            R.id.recipeUpsertFragment, bundleOf(
                "id" to recipeId
            )
        )
    }


}