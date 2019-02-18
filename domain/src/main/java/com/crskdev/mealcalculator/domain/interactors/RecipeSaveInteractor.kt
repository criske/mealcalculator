package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.RecipeDetailed
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.RecipeRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 10.02.2019.
 */
interface RecipeSaveInteractor {

    suspend fun request(recipe: RecipeDetailed, response: (Response) -> Unit)


    sealed class Response {
        class OK(val recipeId: Long) : Response()
        object EmptyName : Response()
        object EmptyRecipe : Response()
    }

}


class RecipeSaveInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val recipeRepository: RecipeRepository
) : RecipeSaveInteractor {

    override suspend fun request(recipeDetailed: RecipeDetailed, response: (RecipeSaveInteractor.Response) -> Unit) =
        coroutineScope {

            if (recipeDetailed.name.isBlank()) {
                response(RecipeSaveInteractor.Response.EmptyName)
                return@coroutineScope
            }

            launch(dispatchers.IO) {
                recipeRepository.runTransaction {
                    val recipe = recipeDetailed.toRecipe()
                    var id = recipe.id
                    if (id <= 0L) { // is new
                        id = save(recipe)
                    } else {
                        update(recipe)
                    }
                    val existentFoods = getRecipeDetailedById(id)?.foods ?: emptyList()
                    val diffResults = RecipeFoodsDiffUtil
                        .calculate(
                            existentFoods,
                            recipeDetailed.foods
                        )
                    diffResults.forEach {
                        when (it) {
                            is RecipeFoodsDiffUtil.Result.Delete ->
                                removeRecipeFood(id, it.food)
                            is RecipeFoodsDiffUtil.Result.Add ->
                                addRecipeFood(id, it.food)
                            is RecipeFoodsDiffUtil.Result.Update ->
                                updateRecipeFood(id, it.food)
                        }
                    }
                    response(RecipeSaveInteractor.Response.OK(id))
                }

            }
        }

}

internal object RecipeFoodsDiffUtil {

    sealed class Result(val food: RecipeFood) {
        class Delete(food: RecipeFood) : Result(food)
        class Add(food: RecipeFood) : Result(food)
        class Update(food: RecipeFood) : Result(food)
    }

    fun calculate(existent: List<RecipeFood>, updated: List<RecipeFood>): List<Result> {
        val results = mutableListOf<RecipeFoodsDiffUtil.Result>()
        updated.forEach {
            val containedInExistent = existent.containedFoodWith(it)
            if (containedInExistent != null) {
                if (it.quantity != containedInExistent.quantity) {
                    results.add(RecipeFoodsDiffUtil.Result.Update(it))
                }
            } else {
                results.add(RecipeFoodsDiffUtil.Result.Add(it))
            }
        }
        existent.forEach {
            if (!updated.containsFood(it)) {
                results.add(RecipeFoodsDiffUtil.Result.Delete(it))
            }
        }
        return results
    }

    private fun List<RecipeFood>.containsFood(recipeFood: RecipeFood): Boolean =
        containedFoodWith(recipeFood) != null

    private fun List<RecipeFood>.containedFoodWith(other: RecipeFood): RecipeFood? =
        firstOrNull { it.id == other.id && it.food.id == other.food.id }


}