package com.crskdev.mealcalculator.domain.entities

/**
 * Created by Cristian Pela on 24.01.2019.
 */
data class Meal(val id: Int = -1,
                val numberOfTheDay: Int = 1,
                val calories: Int,
                val carbohydrate: Carbohydrate,
                val fat: Fat,
                val protein: Float,
                val glycemicLoad: Float,
                val date: String? = null) {
    companion object {
        fun empty(numberOfTheDay: Int = -1, date: String? = null): Meal =
            Meal(
                -1, numberOfTheDay, 0,
                Carbohydrate(0f, 0f, 0f),
                Fat(0f, 0f, 0f),
                0f, 0f, date
            )
    }
}