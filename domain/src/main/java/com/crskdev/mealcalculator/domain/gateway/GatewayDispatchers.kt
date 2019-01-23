package com.crskdev.mealcalculator.domain.gateway

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Created by Cristian Pela on 23.01.2019.
 */
interface GatewayDispatchers {

    val IO: CoroutineDispatcher

    val DEFAULT: CoroutineDispatcher

    val MAIN: CoroutineDispatcher

    val UNCONFINED: CoroutineDispatcher

    fun custom(): CoroutineDispatcher
}

fun GatewayDispatchers.remap(main: CoroutineDispatcher? = null,
                             default: CoroutineDispatcher? = null,
                             io: CoroutineDispatcher? = null,
                             unconfined: CoroutineDispatcher? = null): GatewayDispatchers =
    object : GatewayDispatchers {
        override val IO: CoroutineDispatcher = io ?: this@remap.IO
        override val DEFAULT: CoroutineDispatcher = default ?: this@remap.DEFAULT
        override val MAIN: CoroutineDispatcher = main ?: this@remap.MAIN
        override val UNCONFINED: CoroutineDispatcher = unconfined ?: this@remap.UNCONFINED
        override fun custom(): CoroutineDispatcher = this@remap.custom()
    }