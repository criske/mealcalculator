package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.MealRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by Cristian Pela on 30.01.2019.
 */
interface AllDayMealDisplayInteractor {

    suspend fun request(date: Date, response: (Response) -> Unit)

    sealed class Response {
        class OK(val meal: Meal) : Response()
        object NotFound : Response()
    }
}

class AllDayMealDisplayInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val mealRepository: MealRepository
) : AllDayMealDisplayInteractor {
    override suspend fun request(date: Date, response: (AllDayMealDisplayInteractor.Response) -> Unit) =
        coroutineScope {
            //TODO use for any day not just today
            launch(dispatchers.DEFAULT) {
                val response = mealRepository.getAllTodayMeal()?.let {
                    AllDayMealDisplayInteractor
                        .Response.OK(it)
                } ?: AllDayMealDisplayInteractor.Response.NotFound
                response(response)
            }
            Unit
        }
}