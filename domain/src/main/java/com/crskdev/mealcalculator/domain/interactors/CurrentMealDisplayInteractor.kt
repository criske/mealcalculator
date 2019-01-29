package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat
import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.MealRepository
import com.crskdev.mealcalculator.domain.internal.DateString
import com.crskdev.mealcalculator.domain.internal.MealSummaryCalculator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 24.01.2019.
 */
interface CurrentMealDisplayInteractor {

    suspend fun request(response: (Meal) -> Unit)
}


class CurrentMealDisplayInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val mealRepository: MealRepository) : CurrentMealDisplayInteractor {

    override suspend fun request(response: (Meal) -> Unit) = coroutineScope {
        launch(dispatchers.DEFAULT) {
            mealRepository.observeCurrentMealEntries {
                val mealSummary = if (it.isEmpty()) {
                    val m = mealRepository.getAllTodayMeal() ?: Meal.empty(0, 0, DateString())
                    m.copy(
                        numberOfTheDay = m.numberOfTheDay + 1,
                        carbohydrate = Carbohydrate(0f, 0f, 0f),
                        fat = Fat(0f, 0f, 0f),
                        protein = 0f,
                        glycemicLoad = 0f
                    )
                } else
                    MealSummaryCalculator().calculate(it)
                response(mealSummary)
            }
        }
        Unit
    }

}
