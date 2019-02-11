package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.RecipeFoodEntriesManager
import com.crskdev.mealcalculator.domain.gateway.RecipeRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 10.02.2019.
 */
interface RecipeFoodEntriesUpsertDisplayInteractor {
    suspend fun request(recipeId: Long, response: (List<RecipeFood>) -> Unit)
}

class RecipeFoodEntriesUpsertDisplayInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val recipeFoodEntriesManager: RecipeFoodEntriesManager,
    private val recipeRepository: RecipeRepository
) : RecipeFoodEntriesUpsertDisplayInteractor {
    override suspend fun request(recipeId: Long, response: (List<RecipeFood>) -> Unit) =
        coroutineScope {
            launch(dispatchers.DEFAULT) {
                recipeFoodEntriesManager.observeAll {
                    response(it)
                }
                recipeFoodEntriesManager.addAll(
                    recipeRepository
                        .getRecipeById(recipeId)
                        ?.foods
                        ?: emptyList()
                )
            }
            Unit
        }


}