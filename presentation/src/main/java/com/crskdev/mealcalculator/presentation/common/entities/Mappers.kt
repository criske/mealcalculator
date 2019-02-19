package com.crskdev.mealcalculator.presentation.common.entities

import com.crskdev.mealcalculator.domain.entities.*

/**
 * Created by Cristian Pela on 26.01.2019.
 */

fun RecipeFood.Summary.toVM() = RecipeFoodVM.SummaryVM(
    calories.toString(),
    CarbohydrateVM(
        carbohydrates.total.toFormat(),
        carbohydrates.fiber.toFormat(),
        carbohydrates.sugar.toFormat()
    ),
    FatVM(
        fat.total.toFormat(),
        fat.saturated.toFormat(),
        fat.unsaturated.toFormat()
    ),
    proteins.toFormat(),
    gi.toFormat()
)


fun Food.toVM(): FoodVM =
    FoodVM(
        id,
        name,
        picture,
        calories.toString(),
        carbohydrates.toVM(),
        fat.toVM(),
        proteins.toFormat(),
        gi.toFormat()
    )

fun Carbohydrate.toVM(): CarbohydrateVM =
    CarbohydrateVM(total.toFormat(), fiber.toFormat(), sugar.toFormat())

fun Fat.toVM(): FatVM =
    FatVM(total.toFormat(), saturated.toFormat(), unsaturated.toFormat())

fun FoodVM.toDomain(): Food = Food(
    id, name, picture, calories.toInt(),
    Carbohydrate(
        carbohydrates.total.float,
        carbohydrates.fiber.float,
        carbohydrates.sugar.float
    ),
    Fat(
        fat.total.float,
        fat.saturated.float,
        fat.unsaturated.float
    ),
    proteins.float,
    gi.float
)

fun FoodVM.toDomainUnchecked(): FoodUnchecked =
    FoodUnchecked(
        id, name, picture, calories,
        CarbohydrateUnchecked(
            carbohydrates.total.toString(),
            carbohydrates.fiber.toString(),
            carbohydrates.sugar.toString()
        ),
        FatUnchecked(
            fat.total.toString(),
            fat.saturated.toString(),
            fat.unsaturated.toString()
        ),
        proteins.toString(), gi.toString()
    )

// other mappers
fun Float.toFormat() = FloatFormat(this)


fun String?.toFloatFormat(): FloatFormat = this?.let {
    try {
        if (isBlank()) {
            FloatFormat.ZERO
        } else {
            it.toFloat().toFormat()
        }
    } catch (ex: Exception) {
        FloatFormat.ZERO
    }
} ?: FloatFormat.ZERO

fun String?.toSafeInt(): Int = this?.toIntOrNull() ?: 0