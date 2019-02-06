package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.RecipeFoodEntriesManager
import com.crskdev.mealcalculator.domain.internal.RecipeCalculator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 24.01.2019.
 */
interface RecipeSummaryInteractor {

    suspend fun request(response: (RecipeFood.Summary) -> Unit)
}


class RecipeSummaryInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val recipeFoodEntriesManager: RecipeFoodEntriesManager) : RecipeSummaryInteractor {

    override suspend fun request(response: (RecipeFood.Summary) -> Unit) = coroutineScope {
        launch(dispatchers.DEFAULT) {
            recipeFoodEntriesManager.observeAll {
                val summary = if (it.isEmpty()) {
                    RecipeFood.Summary.EMPTY
                } else
                    RecipeCalculator().calculate(it)
                response(summary)
            }
        }
        Unit
    }

}
