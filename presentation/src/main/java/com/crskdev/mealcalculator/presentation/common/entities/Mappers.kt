package com.crskdev.mealcalculator.presentation.common.entities

import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat
import com.crskdev.mealcalculator.domain.entities.Food

/**
 * Created by Cristian Pela on 26.01.2019.
 */
fun Food.toVM(): FoodVM =
    FoodVM(
        id,
        name,
        picture,
        calories.toString(),
        carbohydrates.toVM(),
        fat.toVM(),
        proteins.toString(),
        gi.toString()
    )

fun Carbohydrate.toVM(): CarbohydrateVM =
    CarbohydrateVM(total.toString(), fiber.toString(), sugar.toString())

fun Fat.toVM(): FatVM =
    FatVM(total.toString(), unsaturated.toString(), saturated.toString())

fun FoodVM.toDomain(): Food = Food(
    id, name, picture, calories.toInt(),
    Carbohydrate(
        carbohydrates.total.toFloat(),
        carbohydrates.fiber.toFloat(),
        carbohydrates.sugar.toFloat()
    ),
    Fat(
        fat.total.toFloat(),
        fat.saturated.toFloat(),
        fat.unsaturated.toFloat()
    ),
    proteins.toFloat(),
    gi.toInt()
)