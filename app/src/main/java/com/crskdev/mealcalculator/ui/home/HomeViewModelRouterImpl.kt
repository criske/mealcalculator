package com.crskdev.mealcalculator.ui.home

import androidx.fragment.app.Fragment
import com.crskdev.mealcalculator.platform.router.PlatformBaseRouter
import com.crskdev.mealcalculator.presentation.food.FindFoodRouter
import com.crskdev.mealcalculator.presentation.home.HomeViewModelRouter
import com.crskdev.mealcalculator.presentation.meal.MealRouter
import com.crskdev.mealcalculator.presentation.recipe.RecipeDisplayRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
class HomeViewModelRouterImpl(provider: () -> Fragment?,
                              private val findFoodRouter: FindFoodRouter,
                              private val mealRouter: MealRouter,
                              private val recipeDisplayRouter: RecipeDisplayRouter) :
    PlatformBaseRouter(provider),
    HomeViewModelRouter {

    override fun routeToFoods() {
        findFoodRouter.routeToFindFoodNoBackResult()
    }

    override fun routeToNewMeal() {
        mealRouter.routeToMealNew()
    }

    override fun routeToMealJournal() {
        mealRouter.routeToMealJournal()
    }

    override fun routeToRecipes() {
        recipeDisplayRouter.routeToRecipesDisplayNoBackResult()
    }

}