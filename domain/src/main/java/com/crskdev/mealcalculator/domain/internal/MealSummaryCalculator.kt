package com.crskdev.mealcalculator.domain.internal

import com.crskdev.mealcalculator.domain.entities.*
import kotlin.math.roundToInt

/**
 * Created by Cristian Pela on 24.01.2019.
 */
internal class MealSummaryCalculator {

    fun calculate(entries: List<MealEntry>): Meal {
        //Glycemic Load = GI x Carbohydrate (g) content per portion ÷ 100.
        //The GL of a mixed meal or diet can simply be calculated by summing together the GL values for each ingredient or component.
        val foods = entries.map {
            it.normalize()
        }

        val glycemicLoad = foods.fold(0) { acc, curr ->
            curr.gi
                ?.times(curr.carbohydrates.total.roundToInt())
                ?.div(100)?.plus(acc)
                ?: 0
        }

        val calories = foods.sumBy { it.calories }

        val fats = foods
            .map { it.fat }
            .fold(Fat(0f, 0f, 0f)) { acc, curr ->
                Fat(
                    acc.total + curr.total,
                    acc.saturated + curr.saturated,
                    acc.unsaturated + acc.unsaturated
                )
            }

        val carbohydrates = foods
            .map { it.carbohydrates }
            .fold(Carbohydrate(0f, 0f, 0f)) { acc, curr ->
                Carbohydrate(
                    acc.total + curr.total,
                    acc.fiber + curr.fiber,
                    acc.sugar + curr.sugar
                )
            }

        val proteins = entries.sumByDouble { it.food.proteins.toDouble() }.toFloat()

        return Meal(-1, 1, calories, carbohydrates, fats, proteins, glycemicLoad)
    }

}