package com.crskdev.mealcalculator.domain.entities

/**
 * Created by Cristian Pela on 24.01.2019.
 */
data class Meal(val id: Long = 0,
                val numberOfTheDay: Int = 1,
                val calories: Int,
                val carbohydrate: Carbohydrate,
                val fat: Fat,
                val protein: Float,
                val glycemicLoad: Float,
                val date: String) {
    companion object {
        fun empty(id: Long = -1, numberOfTheDay: Int = -1, date: String): Meal =
            Meal(
                id, numberOfTheDay, 0,
                Carbohydrate(0f, 0f, 0f),
                Fat(0f, 0f, 0f),
                0f, 0f, date
            )
    }


    operator fun plus(other: Meal): Meal {
        assert(this.id == other.id && this.date == other.date) {
            "In order to compound two meals they must have same id and date"
        }
        val incrementedNumberOfTheDay = this.numberOfTheDay + 1
        return copy(
            numberOfTheDay = incrementedNumberOfTheDay,
            calories = this.calories + other.calories,
            carbohydrate = Carbohydrate(
                this.carbohydrate.total + other.carbohydrate.total,
                this.carbohydrate.fiber + other.carbohydrate.fiber,
                this.carbohydrate.sugar + other.carbohydrate.sugar
            ),
            fat = Fat(
                this.fat.total + other.fat.total,
                this.fat.saturated + other.fat.saturated,
                this.fat.unsaturated + other.fat.unsaturated
            ),
            protein = this.protein + other.protein,
            glycemicLoad = this.glycemicLoad + other.glycemicLoad
        )
    }
}