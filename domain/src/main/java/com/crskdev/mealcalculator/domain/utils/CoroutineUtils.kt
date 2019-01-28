package com.crskdev.mealcalculator.domain.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select

/**
 * Created by Cristian Pela on 24.01.2019.
 */
suspend fun <T> CoroutineScope.switchSelectOnReceive(receiveChannel: ReceiveChannel<T>,
                                                     selectBlock: suspend (Job, T) -> Unit){
    val parent = coroutineContext[Job]
    var switchJob = Job(parent)
    while (isActive) {
        select<Unit> {
            receiveChannel.onReceive {
                switchJob.cancel()
                switchJob = Job(parent)
                selectBlock(switchJob, it)
            }
        }
    }
}