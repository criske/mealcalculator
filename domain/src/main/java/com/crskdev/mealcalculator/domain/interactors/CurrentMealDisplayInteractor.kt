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
interface CurrentMealDisplayInteractor {

    suspend fun request(response: (Meal) -> Unit)
}


class CurrentMealDisplayInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val mealRepository: MealRepository) : CurrentMealDisplayInteractor {

    override suspend fun request(response: (Meal) -> Unit) = coroutineScope {
        launch(dispatchers.DEFAULT) {
            mealRepository.observeCurrentMealEntries {
                val mealSummary = MealSummaryCalculator().calculate(it)
                val currentMeal = mealRepository.getCurrentMeal()
                response(
                    mealSummary.copy(
                        id = currentMeal.id,
                        numberOfTheDay = currentMeal.numberOfTheDay,
                        date = currentMeal.date
                    )
                )
            }
        }
        Unit
    }
    
}
