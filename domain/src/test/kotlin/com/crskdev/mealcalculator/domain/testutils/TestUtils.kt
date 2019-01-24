package com.crskdev.mealcalculator.domain.testutils

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 24.01.2019.
 */


suspend fun <T> ReceiveChannel<T>.cancelDelayed(delayMs: Long = 500L): Unit = coroutineScope {
    delay(delayMs)
    cancel()
    Unit
}

suspend fun <T> SendChannel<T>.closeDelayed(delayMs: Long = 500L): Unit = coroutineScope {
    delay(delayMs)
    close()
    Unit
}