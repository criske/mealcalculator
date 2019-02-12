package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Recipe
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.RecipeRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 12.02.2019.
 */
interface RecipesGetInteractor {

    suspend fun request(response: (List<Recipe>) -> Unit)

}

class RecipesGetInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val recipeRepository: RecipeRepository
) : RecipesGetInteractor {

    override suspend fun request(response: (List<Recipe>) -> Unit) = coroutineScope {
        launch(dispatchers.IO) {
            recipeRepository.observeAll {
                response(it)
            }
        }
        Unit
    }

}