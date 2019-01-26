package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.gateway.FoodRepository
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 26.01.2019.
 */
interface GetFoodInteractor {

    suspend fun request(id: Long, response: (Response) -> Unit)

    sealed class Response {
        class OK(val food: Food) : Response()
        class NotFound(val id: Long) : Response()
    }
}

class GetFoodInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val foodRepository: FoodRepository
) : GetFoodInteractor {

    override suspend fun request(id: Long, response: (GetFoodInteractor.Response) -> Unit) =
        coroutineScope {
            launch(dispatchers.DEFAULT) {
                foodRepository.findById(id)
                    ?.let { response(GetFoodInteractor.Response.OK(it)) }
                    ?: response(GetFoodInteractor.Response.NotFound(id))
            }
            Unit
        }

}