package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.MealEntry
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.MealRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 24.01.2019.
 */
interface CurrentMealEntryDisplayInteractor {

    suspend fun request(response: (List<MealEntry>) -> Unit)

}

class CurrentMealEntryDisplayInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val mealRepository: MealRepository
) : CurrentMealEntryDisplayInteractor {

    override suspend fun request(response: (List<MealEntry>) -> Unit) = coroutineScope {
        launch(dispatchers.DEFAULT) {
            mealRepository.observeCurrentMeal {
                response(it)
            }
        }
        Unit
    }
}