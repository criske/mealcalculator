package com.crskdev.mealcalculator.platform

import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Created by Cristian Pela on 27.01.2019.
 */
object PlatformGatewayDispatchers : GatewayDispatchers {
    override val IO: CoroutineDispatcher
        get() = Dispatchers.IO
    override val DEFAULT: CoroutineDispatcher
        get() = Dispatchers.Default
    override val MAIN: CoroutineDispatcher
        get() = Dispatchers.Main
    override val UNCONFINED: CoroutineDispatcher
        get() = Dispatchers.Unconfined

    override fun custom(): CoroutineDispatcher {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}