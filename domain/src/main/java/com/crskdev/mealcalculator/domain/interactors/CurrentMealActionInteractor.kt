package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.entities.MealEntry
import com.crskdev.mealcalculator.domain.gateway.CurrentMealEntryManager
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
            object NegativeQuantity : Error()
            object MealNotStarted : Error()
            object EmptyMeal : Error()
            class Other(val throwable: Throwable) : Error()
        }
    }

}

class CurrenMealActionInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val mealRepository: MealRepository,
    private val currentMealEntryManager: CurrentMealEntryManager
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
                    var finalResponse: Response = Response.OK
                    when (request) {
                        is Request.StartMeal -> {
                            mealRepository.runTransaction {
                                discardCurrentMealEntries()
                                val todayMealNumber = getAllTodayMealCount()
                                val date = DateString()
                                val id = getAllTodayMealId()
                                val startedMeal = Meal.empty(id, todayMealNumber + 1, date)
                                response(Response.MealStarted(startedMeal))
                            }
                        }
                        is Request.AddEntry -> {
                            checkQuantity(request.entry.quantity)

                            val existentMealWithFood =
                                currentMealEntryManager.getEntryWithFoodId(request.entry.food.id)
                            //update quantity if food already exists and request quantity is different
                            if (existentMealWithFood?.food?.id == request.entry.food.id) {
                                if (request.entry.quantity > 0) {
                                    currentMealEntryManager.update(
                                        existentMealWithFood.copy(
                                            quantity = request.entry.quantity
                                        )
                                    )
                                }
                            } else {
                                currentMealEntryManager + request.entry
                            }


                        }
                        is Request.EditEntry -> {
                            checkQuantity(request.entry.quantity)
                            currentMealEntryManager.update(request.entry)
                        }
                        is Request.RemoveEntry -> {
                            currentMealEntryManager.minus(request.entry)
                        }
                        is Request.SaveMeal -> {
                            if (request.meal.calories > 0) {
                                mealRepository.runTransaction {
                                    if (request.meal.id == 0L) {//not created the daily meal
                                        startAllTodayMeal(request.meal)
                                    } else {
                                        val allTodayMeal = getAllTodayMeal()
                                            ?: throw Response.Error.MealNotStarted
                                        saveAllToday(allTodayMeal + request.meal)
                                    }
                                    discardCurrentMealEntries()
                                    finalResponse = Response.MealSaved
                                }
                            } else {
                                finalResponse = Response.Error.EmptyMeal
                            }
                        }
                        is Request.DiscardMeal -> {
                            mealRepository.runTransaction {
                                discardCurrentMealEntries()
                                finalResponse = Response.MealDiscarded
                            }
                        }
                    }
                    response(finalResponse)
                }
            }
        }

    private fun checkQuantity(quantity: Int) {
        if (quantity < 0)
            throw Response.Error.NegativeQuantity
    }

}