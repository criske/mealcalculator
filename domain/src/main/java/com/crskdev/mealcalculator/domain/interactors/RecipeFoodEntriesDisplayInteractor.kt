package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.RecipeFoodEntriesManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 12.02.2019.
 */
interface RecipeFoodEntriesDisplayInteractor {

    suspend fun request(response: (List<RecipeFood>) -> Unit)

}

class RecipeFoodEntriesDisplayInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val recipeFoodEntriesManager: RecipeFoodEntriesManager) :
    RecipeFoodEntriesDisplayInteractor {

    override suspend fun request(response: (List<RecipeFood>) -> Unit) = coroutineScope {
        launch(dispatchers.DEFAULT) {
            recipeFoodEntriesManager.observeAll {
                response(it)
            }
        }
        Unit
    }
}