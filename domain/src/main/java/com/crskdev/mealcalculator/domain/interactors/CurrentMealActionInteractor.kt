package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.entities.MealEntry
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.MealRepository
import com.crskdev.mealcalculator.domain.interactors.CurrentMealActionInteractor.*
import com.crskdev.mealcalculator.domain.internal.DateString
import com.crskdev.mealcalculator.domain.utils.switchSelectOnReceive
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

/**
 * Created by Cristian Pela on 24.01.2019.
 */
interface CurrentMealActionInteractor {

    suspend fun request(action: ReceiveChannel<Request>, response: (Response) -> Unit)

    sealed class Request {
        object StartMeal : Request()
        class AddEntry(val entry: MealEntry) : Request()
        class EditEntry(val entry: MealEntry) : Request()
        class RemoveEntry(val entry: MealEntry) : Request()
        class SaveMeal(val meal: Meal) : Request()
        object DiscardMeal : Request()
    }

    interface Response {
        object OK : Response
        class MealStarted(val meal: Meal) : Response
        object MealSaved : Response
        object MealDiscarded : Response
        sealed class Error : Throwable(), Response {
            object NegativeOrZeroQuantity : Error()
            class Other(throwable: Throwable) : Error()
        }
    }

}

class CurrenMealActionInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val mealRepository: MealRepository
) : CurrentMealActionInteractor {


    override suspend fun request(action: ReceiveChannel<Request>,
                                 response: (CurrentMealActionInteractor.Response) -> Unit) =
        supervisorScope {
            val errHandler = coroutineContext + CoroutineExceptionHandler { _, throwable ->
                if (throwable is CurrentMealActionInteractor.Response.Error) {
                    response(throwable)
                } else {
                    response(Response.Error.Other(throwable))
                }
            }
            switchSelectOnReceive(action) { job, request ->
                launch(errHandler + job + dispatchers.DEFAULT) {
                    var response: Response = Response.OK
                    when (request) {
                        is Request.StartMeal -> {
                            mealRepository.runTransaction {
                                discardCurrentMealEntries()
                                val mealNumber = getTodayMealCount() + 1
                                val date = DateString()
                                val meal = Meal.empty(mealNumber, date)
                                startCurrentMeal(meal)
                            }
                        }
                        is Request.AddEntry -> {
                            checkQuantity(request.entry.quantity)
                            mealRepository.addMealEntry(request.entry)
                        }
                        is Request.EditEntry -> {
                            checkQuantity(request.entry.quantity)
                            mealRepository.editMealEntry(request.entry)
                        }
                        is Request.RemoveEntry -> {
                            checkQuantity(request.entry.quantity)
                            mealRepository.removeMealEntry(request.entry)
                        }
                        is Request.SaveMeal -> {
                            mealRepository.runTransaction {
                                saveMealToJournal(request.meal)
                                discardCurrentMealEntries()
                                response = Response.MealSaved
                            }
                        }
                        is Request.DiscardMeal -> {
                            mealRepository.runTransaction {
                                discardCurrentMeal()
                                discardCurrentMealEntries()
                                response = Response.MealDiscarded
                            }
                        }
                    }
                    response(response)
                }
            }
        }


    private fun checkQuantity(quantity: Float) {
        if (quantity <= 0)
            throw Response.Error.NegativeOrZeroQuantity
    }

}