package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Recipe
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.RecipeRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 13.02.2019.
 */
interface RecipeDeleteInteractor {

    suspend fun request(recipe: Recipe)

}

class RecipeDeleteInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val recipeRepository: RecipeRepository
) : RecipeDeleteInteractor {

    override suspend fun request(recipe: Recipe) =
        coroutineScope {
            launch(dispatchers.IO) {
                recipeRepository.delete(recipe)
            }
            Unit
        }

}