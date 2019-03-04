package com.crskdev.mealcalculator.presentation.food

/**
 * Created by Cristian Pela on 04.03.2019.
 */
interface UpsertFoodRouter {

    fun routeToUpsertFoodEdit(foodId: Long)

    fun routeToUpsertFoodNew(withName: String? = null)

}

