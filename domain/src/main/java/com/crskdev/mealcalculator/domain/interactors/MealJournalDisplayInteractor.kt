package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.MealRepository
import com.crskdev.mealcalculator.domain.utils.switchSelectOnReceive
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by Cristian Pela on 01.02.2019.
 */
interface MealJournalDisplayInteractor {


    suspend fun request(request: ReceiveChannel<Request>, response: (List<Meal>) -> Unit)

    sealed class Request {
        object All : Request()
        class Interval(val start: Date, val end: Date) : Request()
    }

}

class MealJournalDisplayInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val mealRepository: MealRepository
) : MealJournalDisplayInteractor {

    override suspend fun request(request: ReceiveChannel<MealJournalDisplayInteractor.Request>, response: (List<Meal>) -> Unit) =
        coroutineScope {
            switchSelectOnReceive(request) { job, r ->
                when (r) {
                    is MealJournalDisplayInteractor.Request.All -> {
                        launch(job + dispatchers.DEFAULT) {
                            mealRepository.observeJournalMeals {
                                response(it)
                            }
                        }
                    }
                    is MealJournalDisplayInteractor.Request.Interval -> {
                        response(emptyList())
                    }
                }
            }
        }

}