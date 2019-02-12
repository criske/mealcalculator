package com.crskdev.mealcalculator.presentation.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.Recipe
import com.crskdev.mealcalculator.presentation.common.livedata.SingleLiveEvent
import com.crskdev.mealcalculator.presentation.common.livedata.mutableSet

/**
 * Created by Cristian Pela on 12.02.2019.
 */


open class EventBusViewModel<T> : ViewModel() {

    val eventLiveData: LiveData<Event<T>> = SingleLiveEvent<Event<T>>()

    fun sendEvent(event: Event<T>) {
        eventLiveData.mutableSet(event)
    }

    class Event<T>(val code: Int, val data: T) {
        companion object {
            const val NO_CODE = -1
        }
    }

}

class SelectedFoodViewModel : EventBusViewModel<Food>()
class SelectedRecipeViewModel : EventBusViewModel<Recipe>()
