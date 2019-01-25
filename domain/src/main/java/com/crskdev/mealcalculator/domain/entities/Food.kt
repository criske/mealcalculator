package com.crskdev.mealcalculator.domain.entities

/**
 *
 *
id

name

calories_100g

carbs_100g

proteins_100g

fats_100g

fats_satured_100g

fats_unsaturated_100g

glycemic_index

 * Created by Cristian Pela on 23.01.2019.
 */

data class Food(val id: Long = 0,
                val name: String,
                val picture: String?,
                val calories: Int,
                val carbohydrates: Carbohydrate,
                val fat: Fat,
                val proteins: Float,
                val gi: Int)

data class Fat(val total: Float, val saturated: Float, val unsaturated: Float)

data class Carbohydrate(val total: Float, val fiber: Float = 0f, val sugar: Float)
