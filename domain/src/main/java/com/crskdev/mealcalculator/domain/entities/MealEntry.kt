package com.crskdev.mealcalculator.domain.entities

import kotlin.math.roundToInt

/**
 * Created by Cristian Pela on 24.01.2019.
 */
data class MealEntry(val quantity: Float, val food: Food)


fun MealEntry.normalize(): Food =
    Food(
        food.id,
        food.name,
        food.picture,
        ((quantity * food.calories) / 100).roundToInt(),
        Carbohydrate(
            ((quantity * food.carbohydrates.total) / 100),
            ((quantity * food.carbohydrates.sugar) / 100),
            ((quantity * food.carbohydrates.fiber) / 100)
        ),
        Fat(
            ((quantity * food.fat.total) / 100),
            ((quantity * food.fat.saturated) / 100),
            ((quantity * food.fat.unsaturated) / 100)
        ),
        ((quantity * food.proteins) / 100),
        food.gi
    )