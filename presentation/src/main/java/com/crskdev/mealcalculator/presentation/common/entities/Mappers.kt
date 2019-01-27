package com.crskdev.mealcalculator.presentation.common.entities

import com.crskdev.mealcalculator.domain.entities.*

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

fun FoodVM.toDomainUnchecked(): FoodUnchecked =
    FoodUnchecked(
        id, name, picture, calories,
        CarbohydrateUnchecked(
            carbohydrates.total,
            carbohydrates.fiber,
            carbohydrates.sugar
        ),
        FatUnchecked(
            fat.total,
            fat.saturated,
            fat.unsaturated
        ),
        proteins, gi
    )