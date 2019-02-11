package com.crskdev.mealcalculator.data.internal.room.entities

/**
 * Created by Cristian Pela on 25.01.2019.
 */
internal data class FatDb(var total: Float, var saturated: Float, var unsaturated: Float)

internal data class CarbohydrateDb(var total: Float, var fiber: Float = 0f, var sugar: Float)

