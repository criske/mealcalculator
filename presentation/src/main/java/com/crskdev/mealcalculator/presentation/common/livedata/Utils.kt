package com.crskdev.mealcalculator.presentation.common.livedata

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.crskdev.mealcalculator.presentation.common.utils.cast
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor

/**
 * Created by Cristian Pela on 26.01.2019.
 */
fun <T> LiveData<T>.mutableSet(value: T) {
    assert(this is MutableLiveData)
    if (Looper.myLooper() == Looper.getMainLooper()) {
        this.cast<MutableLiveData<T>>().value = value
    } else {
        this.cast<MutableLiveData<T>>().postValue(value)
    }
}

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
suspend fun <T> LiveData<T>.toChannel(mainDispatcher: CoroutineDispatcher = Dispatchers.Main, block: suspend (ReceiveChannel<T>) -> Unit) =
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
