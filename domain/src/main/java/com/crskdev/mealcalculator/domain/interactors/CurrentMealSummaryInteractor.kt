package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.MealRepository
import com.crskdev.mealcalculator.domain.internal.MealSummaryCalculator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 24.01.2019.
 */
interface CurrentMealSummaryInteractor {

    suspend fun request(response: (Meal) -> Unit)
}


class CurrentMealSummaryInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val mealRepository: MealRepository) : CurrentMealSummaryInteractor {
    override suspend fun request(response: (Meal) -> Unit) = coroutineScope {
        launch(dispatchers.DEFAULT) {
            mealRepository.observeCurrentMeal {
                val mealSummary = MealSummaryCalculator().calculate(it)
                val mealOfTheDay = mealRepository.getTodayMealCount()
                response(mealSummary.copy(numberOfTheDay = mealOfTheDay))
            }
        }
        Unit
    }
}
