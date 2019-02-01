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
interface MealJournalDetailInteractor {

    suspend fun request(mealId: Long, response: (Response) -> Unit)

    sealed class Response {
        class OK(val meal: Meal) : Response()
        object NotFound : Response()
    }
}

class MealJournalDetailInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val mealRepository: MealRepository
) : MealJournalDetailInteractor {
    override suspend fun request(mealId: Long, response: (MealJournalDetailInteractor.Response) -> Unit) =
        coroutineScope {
            launch(dispatchers.DEFAULT) {
                val response =
                    if (mealId <= 0) {
                        mealRepository.getAllTodayMeal()?.let {
                            MealJournalDetailInteractor
                                .Response.OK(it)
                        } ?: MealJournalDetailInteractor.Response.NotFound
                    } else {
                        mealRepository.getJournalMealById(mealId).let {
                            MealJournalDetailInteractor
                                .Response.OK(it)
                        }
                    }
                response(response)
            }
            Unit
        }
}