package com.crskdev.mealcalculator.domain.internal

import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat
import com.crskdev.mealcalculator.domain.entities.RecipeFood

/**
 * Created by Cristian Pela on 24.01.2019.
 */
internal class RecipeCalculator {

    fun calculate(entries: List<RecipeFood>): RecipeFood.Summary {
        assert(entries.isNotEmpty())

        val foods = entries.map { it.getSummary() }

        val carbohydrates = foods
            .map { it.carbohydrates }
            .fold(Carbohydrate(0f, 0f, 0f)) { acc, curr ->
                Carbohydrate(
                    acc.total + curr.total,
                    acc.fiber + curr.fiber,
                    acc.sugar + curr.sugar
                )
            }

        val glycemicLoad = foods.sumByDouble { it.gi.toDouble() }.toFloat()

        assert(glycemicLoad >= 0)

        val calories = foods.sumBy { it.calories }

        val fats = foods
            .map { it.fat }
            .fold(Fat(0f, 0f, 0f)) { acc, curr ->
                Fat(
                    acc.total + curr.total,
                    acc.saturated + curr.saturated,
                    acc.unsaturated + curr.unsaturated
                )
            }

        val proteins = foods.sumByDouble { it.proteins.toDouble() }.toFloat()


        return RecipeFood.Summary(calories, carbohydrates, fats, proteins, glycemicLoad)
    }

}