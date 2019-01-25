package com.crskdev.mealcalculator.data.internal.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor

/**
 * Created by Cristian Pela on 25.01.2019.
 */
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
internal suspend fun <T> LiveData<T>.toChannel(mainDispatcher: CoroutineDispatcher = Dispatchers.Main, block: suspend (ReceiveChannel<T>) -> Unit) =
    coroutineScope {
        val sendChannel = actor<T> {
            block(channel)
        }
        val observer = Observer<T> {
            launch {
                sendChannel.send(it)
            }
        }
        launch(mainDispatcher) {
            observeForever(observer)
        }
        sendChannel.invokeOnClose {
            this@toChannel.removeObserver(observer)
        }
    }
