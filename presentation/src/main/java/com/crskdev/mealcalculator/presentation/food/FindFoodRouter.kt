package com.crskdev.mealcalculator.presentation.food

import com.crskdev.mealcalculator.presentation.common.SourceID

/**
 * Created by Cristian Pela on 04.03.2019.
 */
interface FindFoodRouter {

    fun routeToFindFood(sourceId: SourceID, subSourceId: SourceID)

    fun routeToFindFoodNoBackResult()

}