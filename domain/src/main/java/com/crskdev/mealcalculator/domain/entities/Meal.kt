package com.crskdev.mealcalculator.domain.entities

import com.crskdev.mealcalculator.domain.utils.absoluteCoercedValue

/**
 * Created by Cristian Pela on 24.01.2019.
 */
data class Meal(val id: Long = 0,
                val numberOfTheDay: Int = 1,
                val summary: RecipeFood.Summary,
                val date: String) {
    companion object {
        fun empty(id: Long = -1, numberOfTheDay: Int = -1, date: String): Meal =
            Meal(id, numberOfTheDay, RecipeFood.Summary.EMPTY, date)
    }


    operator fun plus(other: Meal): Meal {
        assert(this.id == other.id && this.date == other.date) {
            "In order to compound two meals they must have same id and date"
        }
        val incrementedNumberOfTheDay = this.numberOfTheDay + 1
        val summary = RecipeFood.Summary(
            this.summary.calories + other.summary.calories,
            Carbohydrate(
                (this.summary.carbohydrates.total + other.summary.carbohydrates.total).absoluteCoercedValue,
                (this.summary.carbohydrates.fiber + other.summary.carbohydrates.fiber).absoluteCoercedValue,
                (this.summary.carbohydrates.sugar + other.summary.carbohydrates.sugar).absoluteCoercedValue
            ),
            Fat(
                (this.summary.fat.total + other.summary.fat.total).absoluteCoercedValue,
                (this.summary.fat.saturated + other.summary.fat.saturated).absoluteCoercedValue,
                (this.summary.fat.unsaturated + other.summary.fat.unsaturated).absoluteCoercedValue
            ),
            (this.summary.proteins + other.summary.proteins).absoluteCoercedValue,
            (this.summary.gi + other.summary.gi).absoluteCoercedValue
        )
        return copy(
            numberOfTheDay = incrementedNumberOfTheDay,
            summary = summary
        )
    }

}