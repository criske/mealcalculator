package com.crskdev.mealcalculator.presentation.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.crskdev.mealcalculator.presentation.common.EventBusViewModel.Event.Companion.NO_CODE
import com.crskdev.mealcalculator.presentation.common.livedata.SingleLiveEvent
import com.crskdev.mealcalculator.presentation.common.livedata.mutableSet

/**
 * Created by Cristian Pela on 12.02.2019.
 */


class EventBusViewModel : ViewModel() {

    val eventLiveData: LiveData<Event<Any>> = SingleLiveEvent<Event<Any>>()

    fun sendEvent(event: Event<Any>) {
        eventLiveData.mutableSet(event)
    }

    class Event<T>(val code: Code, val data: T) {
        constructor(target: Int, data: T) : this(Code(target), data)

        companion object {
            const val NO_CODE = -1
        }
    }

    data class Code(val target: Int, val source: Int = NO_CODE)

}