package com.crskdev.mealcalculator.data.internal.room.entities

import com.crskdev.mealcalculator.domain.entities.*
import kotlin.math.roundToInt

/**
 * Created by Cristian Pela on 25.01.2019.
 */
///mappers
internal fun FoodDb.toDomain(): Food =
    Food(
        id,
        name,
        picture,
        calories,
        carbohydrate.toDomain(),
        fat.toDomain(),
        proteins,
        gi.toFloat()
    )

internal fun Food.toDb(): FoodDb =
    FoodDb(id, name, picture, calories, carbohydrates.toDb(), fat.toDb(), proteins, gi.roundToInt())

internal fun MealDb.toDomain(): Meal =
    Meal(
        id,
        numberOfTheDay,
        RecipeFood.Summary(
            calories,
            carbohydrate.toDomain(),
            fat.toDomain(),
            protein,
            glycemicLoad
        ),
        date
    )

internal fun Meal.toDb(): MealDb =
    MealDb(
        id,
        numberOfTheDay,
        summary.calories,
        summary.carbohydrates.toDb(),
        summary.fat.toDb(),
        summary.proteins,
        summary.gi,
        date
    )

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

internal fun Recipe.toDb() = RecipeDb(id, name)

internal fun RecipeDb.toDomain() = Recipe(id, name, null)

internal fun RecipeDbWithFoodNames.toDomain() = Recipe(recipe.id, recipe.name, foodNames)

internal fun List<RecipeDetailedDb>.toDomain(): RecipeDetailed {
    assert(isNotEmpty())
    val (id, name) = first().let { it.recipe.id to it.recipe.name }
    val foods = filter { it.food != null && it.quantity != null }
        .map { RecipeFood(it.id, it.food!!.toDomain(), it.quantity!!) }
    return RecipeDetailed(id, name, foods)
}

internal fun RecipeFood.toDb(recipeId: Long) = RecipeFoodDb(id, recipeId, food.id, quantity)