package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.MealRepository
import com.crskdev.mealcalculator.domain.internal.DateString
import com.crskdev.mealcalculator.domain.internal.RecipeCalculator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 06.02.2019.
 */
interface CurrentMealSaveInteractor {

    suspend fun request(request: List<RecipeFood>, response: (Response) -> Unit)

    sealed class Response {
        object OK : Response()
        object NotSaved : Response()
    }
}

class CurrentMealSaveInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val mealRepository: MealRepository
) : CurrentMealSaveInteractor {
    override suspend fun request(request: List<RecipeFood>, response: (CurrentMealSaveInteractor.Response) -> Unit) =
        coroutineScope {
            if (request.isNotEmpty()) {
                launch(dispatchers.DEFAULT) {
                    mealRepository.runTransaction {
                        val summary = RecipeCalculator().calculate(request)
                        val allDayMeal = mealRepository.getAllTodayMeal()?.let {
                            val toAddMeal = Meal(
                                it.id,
                                it.numberOfTheDay,
                                summary.calories,
                                summary.carbohydrates,
                                summary.fat,
                                summary.proteins,
                                summary.gi,
                                it.date
                            )
                            it + toAddMeal
                        }
                        if (allDayMeal != null) {
                            saveAllToday(allDayMeal)
                        } else {
                            startAllTodayMeal(
                                Meal(
                                    0, 1, summary.calories,
                                    summary.carbohydrates,
                                    summary.fat,
                                    summary.proteins,
                                    summary.gi,
                                    DateString()
                                )
                            )
                        }
                        response(CurrentMealSaveInteractor.Response.OK)
                    }
                }
            } else {
                response(CurrentMealSaveInteractor.Response.NotSaved)
            }

            Unit
        }
}