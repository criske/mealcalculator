package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.RecipeFoodEntriesManager
import com.crskdev.mealcalculator.domain.gateway.RecipeRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 24.01.2019.
 */
interface RecipeFoodEntriesDisplayInteractor {

    suspend fun request(recipeId: Long, response: (List<RecipeFood>) -> Unit)

}

class RecipeFoodEntriesDisplayInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val recipeFoodEntriesManager: RecipeFoodEntriesManager,
    private val recipeRepository: RecipeRepository
) : RecipeFoodEntriesDisplayInteractor {

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