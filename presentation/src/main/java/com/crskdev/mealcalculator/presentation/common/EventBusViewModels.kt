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
        constructor(target: TargetID, data: T) : this(Code(target), data)
        constructor(target: TargetID, targetSubId: TargetID, data: T) : this(
            Code(
                target,
                targetSubId
            ), data
        )

        companion object {
            const val NO_CODE = -1
        }
    }

    data class Code(val targetId: TargetID,
                    val targetSubId: TargetID = TargetID(NO_CODE),
                    val sourceId: SourceID = SourceID(NO_CODE),
                    val sourceSubId: SourceID = SourceID(NO_CODE))

}


inline class TargetID(val value: Int)
inline class SourceID(val value: Int)

fun Int.asTargetID(): TargetID = TargetID(this)
fun Int.asSourceID(): SourceID = SourceID(this)
fun SourceID.toTargetId(): TargetID = TargetID(value)
fun TargetID.toSourceId(): SourceID = SourceID(value)