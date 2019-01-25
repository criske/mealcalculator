package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.gateway.FoodRepository
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.interactors.FindFoodInteractor.Response
import com.crskdev.mealcalculator.domain.utils.switchSelectOnReceive
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 24.01.2019.
 */
interface FindFoodInteractor {

    suspend fun request(searchQuery: ReceiveChannel<String>, response: (Response) -> Unit)

    sealed class Response {

        class FoundList(val list: List<Food>) : Response()
        /**
         * Rule: when response is an empty list. User has the posibility to create
         * a new food item with searched query as name.
         * Searched query should be at least 3 length
         */
        class CreateNewFoodWithQueryNameWhenEmpty(val query: String) : Response()
    }

}


class FindFoodInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val foodRepository: FoodRepository
) : FindFoodInteractor {

    override suspend fun request(searchQuery: ReceiveChannel<String>, response: (Response) -> Unit) {
        coroutineScope {
            switchSelectOnReceive(searchQuery) { job, query ->
                launch(job + dispatchers.DEFAULT) {
                    if (query.isEmpty()) {
                        foodRepository.findAll {
                            response(Response.FoundList(it))
                        }
                    } else {
                        foodRepository.find(query) {
                            response(Response.FoundList(it))
                            if (it.isEmpty() && query.length >= 3) {
                                response(Response.CreateNewFoodWithQueryNameWhenEmpty(query))
                            }
                        }
                    }
                }
            }
            Unit
        }
    }


}