package com.crskdev.mealcalculator.presentation.meal

import com.crskdev.mealcalculator.presentation.common.router.BackRouter
import com.crskdev.mealcalculator.presentation.food.FindFoodRouter
import com.crskdev.mealcalculator.presentation.recipe.RecipeDisplayRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
interface MealViewModelRouter : FindFoodRouter, RecipeDisplayRouter, BackRouter