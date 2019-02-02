package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.MealRepository
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 02.02.2019.
 */
interface MealJournalDeleteInteractor {

    suspend fun request(id: Long)

}

class MealJournalDeleteInteractorImpl(
    private val dispatchers: GatewayDispatchers,
    private val mealRepository: MealRepository
) : MealJournalDeleteInteractor {

    override suspend fun request(id: Long) = coroutineScope {
        launch(dispatchers.DEFAULT) {
            mealRepository.deleteMealFromJournal(id)
        }
        Unit
    }

}