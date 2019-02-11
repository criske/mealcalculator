package com.crskdev.mealcalculator.data.internal.room

import com.crskdev.mealcalculator.data.internal.room.entities.CarbohydrateDb
import com.crskdev.mealcalculator.data.internal.room.entities.FatDb
import com.crskdev.mealcalculator.data.internal.room.entities.FoodDb
import com.crskdev.mealcalculator.data.internal.room.entities.RecipeDb

/**
 * Created by Cristian Pela on 11.02.2019.
 */
internal object Utils {

    val EMPTY_FOOD = FoodDb(
        0, "", null, 0,
        CarbohydrateDb(0f, 0f, 0f), FatDb(0f, 0f, 0f), 0f, 0
    )

    val EMPTY_RECIPE = RecipeDb(0, "")
}