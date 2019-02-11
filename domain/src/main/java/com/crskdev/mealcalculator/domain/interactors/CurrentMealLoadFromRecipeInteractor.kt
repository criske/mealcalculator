package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.RecipeFoodEntriesManager
import com.crskdev.mealcalculator.domain.gateway.RecipeRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 24.01.2019.
 */
interface CurrentMealLoadFromRecipeInteractor {

    suspend fun request(recipeId: Long, conflicts: (List<ConflictingRecipeFood>) -> Unit)

    class ConflictingRecipeFood(
        val existentQuantity: Int,
        val recipeQauntity: Int,
        val food: Food
    )

}

class CurrentMealLoadFromRecipeInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val recipeRepository: RecipeRepository,
    private val recipeFoodEntriesManager: RecipeFoodEntriesManager
) : CurrentMealLoadFromRecipeInteractor {

    override suspend fun request(recipeId: Long, conflictsResponse: (List<CurrentMealLoadFromRecipeInteractor.ConflictingRecipeFood>) -> Unit) =
        coroutineScope {
            launch(dispatchers.DEFAULT) {
                val foodsFromRecipe = recipeRepository
                    .getRecipeById(recipeId)
                    ?.foods
                    ?: emptyList()
                val existentFoods = recipeFoodEntriesManager.getAll()


                val conflictingRecipeFoods =
                    mutableListOf<CurrentMealLoadFromRecipeInteractor.ConflictingRecipeFood>()
                val nonConflictingRecipeFoods = mutableListOf<RecipeFood>()
                foodsFromRecipe.forEach { rf ->
                    val exists = existentFoods.firstOrNull { it.food.id == rf.food.id }
                    if (exists != null) {
                        conflictingRecipeFoods.add(
                            CurrentMealLoadFromRecipeInteractor.ConflictingRecipeFood(
                                exists.quantity,
                                rf.quantity,
                                rf.food
                            )
                        )
                    } else {
                        nonConflictingRecipeFoods.add(rf)
                    }
                }
                if (conflictingRecipeFoods.isNotEmpty()) {
                    conflictsResponse(conflictingRecipeFoods)
                }
                recipeFoodEntriesManager.addAll(nonConflictingRecipeFoods)
            }
            Unit
        }
}