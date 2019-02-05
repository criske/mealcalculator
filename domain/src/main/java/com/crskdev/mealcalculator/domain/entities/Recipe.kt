package com.crskdev.mealcalculator.domain.entities

/**
 * Created by Cristian Pela on 05.02.2019.
 */
data class Recipe(val id: Long, val name: String)

data class RecipeEntry(val recipeId: Long, val food: Food, val quantity: Int)