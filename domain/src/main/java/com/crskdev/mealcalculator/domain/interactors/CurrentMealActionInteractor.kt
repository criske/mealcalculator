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
            object MealNotStarted : Error()
            class Other(val throwable: Throwable) : Error()
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
                    var finalResponse: Response = Response.OK
                    when (request) {
                        is Request.StartMeal -> {
                            mealRepository.runTransaction {
                                discardCurrentMealEntries()
                                val todayMealNumber = getAllTodayMealCount()
                                val date = DateString()
                                if (todayMealNumber == 0) {
                                    startAllTodayMeal(Meal.empty(0, 0, date))
                                }
                                val id = getAllTodayMealId()
                                val startedMeal = Meal.empty(id, todayMealNumber + 1, date)
                                response(Response.MealStarted(startedMeal))
                            }
                        }
                        is Request.AddEntry -> {
                            checkQuantity(request.entry.quantity)
                            mealRepository.runTransaction {
                                val existentMealWithFood =
                                    existentCurrentMealEntryWithFood(request.entry.food.id)
                                //update quantity if food already exists and request quantity is different
                                if (existentMealWithFood?.quantity?.equals(request.entry.quantity)?.not() == true) {
                                    editCurrentMealEntry(existentMealWithFood.copy(quantity = request.entry.quantity))
                                } else {
                                    addCurrentMealEntry(request.entry)
                                }
                            }

                        }
                        is Request.EditEntry -> {
                            checkQuantity(request.entry.quantity)
                            mealRepository.editCurrentMealEntry(request.entry)
                        }
                        is Request.RemoveEntry -> {
                            mealRepository.removeCurrentMealEntry(request.entry)
                        }
                        is Request.SaveMeal -> {
                            mealRepository.runTransaction {
                                val allTodayMeal = getAllTodayMeal()
                                    ?: throw Response.Error.MealNotStarted
                                saveAllToday(allTodayMeal + request.meal)
                                discardCurrentMealEntries()
                                finalResponse = Response.MealSaved
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
        if (quantity <= 0)
            throw Response.Error.NegativeOrZeroQuantity
    }

}