package com.crskdev.mealcalculator.domain.internal

import com.crskdev.mealcalculator.domain.entities.*

/**
 * Created by Cristian Pela on 24.01.2019.
 */
internal class MealSummaryCalculator {

    fun calculate(entries: List<MealEntry>): Meal {
        assert(entries.isNotEmpty())

        //Glycemic Load = GI x Carbohydrate (g) content per portion ÷ 100.
        //The GL of a mixed meal or diet can simply be calculated by summing together the GL values for each ingredient or component.
        val foods = entries.map { it.foodBasedOnQuantity() }

        val carbohydrates = foods
            .map { it.carbohydrates }
            .fold(Carbohydrate(0f, 0f, 0f)) { acc, curr ->
                Carbohydrate(
                    acc.total + curr.total,
                    acc.fiber + curr.fiber,
                    acc.sugar + curr.sugar
                )
            }

//        val glycemicLoad = foods.fold(0f) { acc, curr ->
//            curr.gi
//                .times(curr.carbohydrates.total)
//                .div(100)
//                .plus(acc)
//        }

        val glycemicLoad = foods.sumByDouble { it.gi.toDouble() }.toFloat()

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


        val proteins = foods.sumByDouble { it.proteins.toDouble() }.toFloat()

        val (id, number, date) = entries.first().let {
            Triple(
                it.mealId,
                it.mealNumber,
                it.mealDate
            )
        }

        return Meal(id, number, calories, carbohydrates, fats, proteins, glycemicLoad, date)
    }

}