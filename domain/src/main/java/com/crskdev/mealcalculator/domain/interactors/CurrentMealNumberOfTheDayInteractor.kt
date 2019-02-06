package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.MealRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 05.02.2019.
 */
interface CurrentMealNumberOfTheDayInteractor {

    suspend fun request(response: (Int) -> Unit)

}

class CurrentMealNumberOfTheDayInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val mealRepository: MealRepository
) : CurrentMealNumberOfTheDayInteractor {
    override suspend fun request(response: (Int) -> Unit) = coroutineScope {
        launch(dispatchers.DEFAULT) {
            val number = mealRepository.getAllTodayMealCount() + 1
            response(number)
        }
        Unit
    }
}