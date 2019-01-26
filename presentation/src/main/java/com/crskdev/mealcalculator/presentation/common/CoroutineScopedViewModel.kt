package com.crskdev.mealcalculator.presentation.common

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Created by Cristian Pela on 25.01.2019.
 */

abstract class CoroutineScopedViewModel(mainDispatcher: CoroutineDispatcher = Dispatchers.Main) :
    ViewModel(), CoroutineScope {

    protected val job = Job()

    override val coroutineContext: CoroutineContext = job + mainDispatcher

    @CallSuper
    override fun onCleared() {
        job.cancel()
    }
}