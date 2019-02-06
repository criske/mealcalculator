package com.crskdev.mealcalculator.domain.entities

/**
 * Created by Cristian Pela on 05.02.2019.
 */
data class Recipe(val id: Long, val name: String)

data class RecipeEntry(val recipeId: Long, val food: RecipeFood)

data class RecipeFood(val food: Food, val quantity: Int) {


    class Summary(val calories: Int,
                  val carbohydrates: Carbohydrate,
                  val fat: Fat,
                  val proteins: Float,
                  val gi: Float) {
        companion object {
            val EMPTY = Summary(
                0,
                Carbohydrate(0f, 0f, 0f),
                Fat(0f, 0f, 0f),
                0f,
                0f
            )
        }
    }

    fun getSummary(): Summary {
        val carbs = Carbohydrate(
            ((quantity * food.carbohydrates.total) / 100),
            ((quantity * food.carbohydrates.fiber) / 100),
            ((quantity * food.carbohydrates.sugar) / 100)
        )
        val netCarbs = carbs.total - carbs.fiber
        return Summary(
            (quantity * food.calories) / 100,
            carbs,
            Fat(
                ((quantity * food.fat.total) / 100),
                ((quantity * food.fat.saturated) / 100),
                ((quantity * food.fat.unsaturated) / 100)
            ),
            (quantity * food.proteins) / 100,
            (food.gi / 100) * netCarbs
        )
    }

    operator fun plus(other: RecipeFood): RecipeFood {
        require(this.food.id == other.food.id)
        return copy(quantity = this.quantity + other.quantity)
    }
}
