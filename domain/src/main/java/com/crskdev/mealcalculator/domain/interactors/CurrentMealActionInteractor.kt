package com.crskdev.mealcalculator.domain.interactors

import com.crskdev.mealcalculator.domain.entities.MealEntry
import kotlinx.coroutines.channels.ReceiveChannel

/**
 * Created by Cristian Pela on 24.01.2019.
 */
interface CurrentMealActionInteractor {

    suspend fun request(action: ReceiveChannel<Request>)

    sealed class Request {
        class AddEntry(val entry: MealEntry) : Request()
        class EditEntry(val entry: MealEntry) : Request()
        class RemoveEntry(val entry: MealEntry) : Request()
        object SaveMeal : Request()
        object DiscardMeal : Request()
    }

}