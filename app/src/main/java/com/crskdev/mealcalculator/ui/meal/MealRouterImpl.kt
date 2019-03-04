package com.crskdev.mealcalculator.ui.meal

import androidx.fragment.app.Fragment
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.platform.router.PlatformBaseRouter
import com.crskdev.mealcalculator.presentation.meal.MealRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
class MealRouterImpl(provider: () -> Fragment?) : PlatformBaseRouter(provider), MealRouter {

    override fun routeToMealNew() {
        findNavController()?.navigate(R.id.mealFragment)
    }

    override fun routeToMealJournal() {
        findNavController()?.navigate(R.id.mealJournalFragment)
    }

}