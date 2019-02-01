package com.crskdev.mealcalculator.platform

import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * Created by Cristian Pela on 27.01.2019.
 */
object PlatformGatewayDispatchers : GatewayDispatchers {
    private val customExecutor by lazy {
        Executors.newFixedThreadPool(3)
    }

    override val IO: CoroutineDispatcher
        get() = Dispatchers.IO
    override val DEFAULT: CoroutineDispatcher
        get() = Dispatchers.Default
    override val MAIN: CoroutineDispatcher
        get() = Dispatchers.Main
    override val UNCONFINED: CoroutineDispatcher
        get() = Dispatchers.Unconfined

    override fun custom(): CoroutineDispatcher = customExecutor.asCoroutineDispatcher()
}