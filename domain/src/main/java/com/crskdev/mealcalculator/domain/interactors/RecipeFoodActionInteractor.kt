package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.RecipeFoodEntriesManager
import com.crskdev.mealcalculator.domain.utils.absoluteCoercedValue
import com.crskdev.mealcalculator.domain.utils.switchSelectOnReceive
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

/**
 * Created by Cristian Pela on 06.02.2019.
 */
interface RecipeFoodActionInteractor {

    suspend fun request(action: ReceiveChannel<Request>, response: (Response) -> Unit)

    sealed class Request {
        class AddRecipe(vararg val recipeFoods: RecipeFood) : Request()
        class AddFood(val food: Food) : Request()
        class Edit(val recipeFood: RecipeFood) : Request()
        class Remove(val recipeFood: RecipeFood) : Request()

    }

    sealed class Response {
        object OK : Response()
    }
}


class RecipeFoodActionInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val recipeFoodEntriesManager: RecipeFoodEntriesManager
) : RecipeFoodActionInteractor {

    override suspend fun request(action: ReceiveChannel<RecipeFoodActionInteractor.Request>,
                                 response: (RecipeFoodActionInteractor.Response) -> Unit) =
        supervisorScope {

            switchSelectOnReceive(action) { job, request ->
                launch(job + dispatchers.DEFAULT) {
                    when (request) {
                        is RecipeFoodActionInteractor.Request.AddRecipe -> {
                            request.recipeFoods.forEach { newFood ->
                                if (!recipeFoodEntriesManager.contains(newFood)) {
                                    recipeFoodEntriesManager.add(newFood, false)
                                }
                            }
                            recipeFoodEntriesManager.notifyObservers()
                        }
                        is RecipeFoodActionInteractor.Request.AddFood -> {
                            val recipeFood = RecipeFood(0, request.food, 0)
                            if (!recipeFoodEntriesManager.contains(recipeFood)) {
                                recipeFoodEntriesManager.add(recipeFood)
                            }
                        }

                        is RecipeFoodActionInteractor.Request.Edit -> {
                            val sanitizedQuantity = request.recipeFood.quantity.absoluteCoercedValue
                            if (sanitizedQuantity > 0) {
                                val recipeFood =
                                    request.recipeFood.copy(quantity = sanitizedQuantity)
                                if (recipeFoodEntriesManager.contains(recipeFood)) {
                                    recipeFoodEntriesManager.update(recipeFood)
                                } else {
                                    recipeFoodEntriesManager.add(recipeFood)
                                }
                            }
                        }
                        is RecipeFoodActionInteractor.Request.Remove -> {
                            recipeFoodEntriesManager - request.recipeFood
                        }
                    }
                    response(RecipeFoodActionInteractor.Response.OK)
                }
                Unit
            }

        }

}