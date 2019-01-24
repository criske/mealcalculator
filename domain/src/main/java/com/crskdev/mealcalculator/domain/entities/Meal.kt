package com.crskdev.mealcalculator.domain.entities

/**
 * Created by Cristian Pela on 24.01.2019.
 */
data class Meal(val id: Int = -1,
                val numberOfTheDay : Int= 1,
                val calories: Int,
                val carbohydrate: Carbohydrate,
                val fat: Fat,
                val protein: Float,
                val glycemicLoad: Float,
                val date: String? = null)