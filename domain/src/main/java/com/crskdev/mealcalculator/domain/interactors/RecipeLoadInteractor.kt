package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.RecipeDetailed
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.RecipeFoodEntriesManager
import com.crskdev.mealcalculator.domain.gateway.RecipeRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 10.02.2019.
 */
interface RecipeLoadInteractor {
    suspend fun request(recipeId: Long, editable: Boolean, response: (RecipeDetailed) -> Unit)
}

class RecipeLoadInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val recipeFoodEntriesManager: RecipeFoodEntriesManager,
    private val recipeRepository: RecipeRepository
) : RecipeLoadInteractor {
    override suspend fun request(recipeId: Long, editable: Boolean, response: (RecipeDetailed) -> Unit) =
        coroutineScope {
            launch(dispatchers.DEFAULT) {
                val recipe = recipeRepository.getRecipeById(recipeId)
                require(recipe != null)
                if (editable) {
                    recipeFoodEntriesManager.observeAll {
                        response(recipe.copy(foods = it))
                    }
                    recipeFoodEntriesManager.addAll(
                        recipeRepository
                            .getRecipeById(recipeId)
                            ?.foods
                            ?: emptyList()
                    )
                } else {
                    response(recipe)
                }
            }
            Unit
        }


}