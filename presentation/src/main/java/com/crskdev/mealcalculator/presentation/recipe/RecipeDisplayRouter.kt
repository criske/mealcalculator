package com.crskdev.mealcalculator.presentation.recipe

import com.crskdev.mealcalculator.presentation.common.SourceID

/**
 * Created by Cristian Pela on 04.03.2019.
 */
interface RecipeDisplayRouter {

    fun routeToRecipesDisplay(sourceId: SourceID, subSourceId: SourceID)

    fun routeToRecipesDisplayNoBackResult()

}