package com.crskdev.mealcalculator.data.internal.room.entities

import com.crskdev.mealcalculator.domain.entities.*

/**
 * Created by Cristian Pela on 25.01.2019.
 */
///mappers
internal fun FoodDb.toDomain(): Food =
    Food(id, name, picture, calories, carbohydrate.toDomain(), fat.toDomain(), proteins, gi)

internal fun Food.toDb(): FoodDb =
    FoodDb(id, name, picture, calories, carbohydrates.toDb(), fat.toDb(), proteins, gi)

internal fun MealDb.toDomain(): Meal =
    Meal(id, numberOfTheDay, calories, carbohydrate.toDomain(), fat.toDomain(), protein, glycemicLoad, date)

internal fun Meal.toDb(): MealDb =
    MealDb(id, numberOfTheDay, calories, carbohydrate.toDb(), fat.toDb(), protein, glycemicLoad, date)

internal fun Carbohydrate.toDb(): CarbohydrateDb =
    CarbohydrateDb(total, fiber, sugar)

internal fun CarbohydrateDb.toDomain(): Carbohydrate =
    Carbohydrate(total, fiber, sugar)

internal fun Fat.toDb(): FatDb = FatDb(total, saturated, unsaturated)

internal fun FatDb.toDomain(): Fat = Fat(total, saturated, unsaturated)

internal fun MealEntry.toDb(): MealEntryDb =
    MealEntryDb(id, mealId, mealDate, mealNumber, food.id, quantity)

internal fun MealEntryWithFoodDb.toDomain(): MealEntry =
    MealEntry(
        mealEntry.id,
        mealEntry.mealId,
        mealEntry.mealDate,
        mealEntry.mealNumber,
        mealEntry.quantity,
        food.toDomain()
    )