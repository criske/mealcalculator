package com.crskdev.mealcalculator.utils

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.reflect.KProperty

/**
 * Created by Cristian Pela on 15.02.2019.
 */
fun <T : LifecycleObserver> LifecycleOwner.lifecycleRegistry(initializer: () -> T) =
    LifeCycleRegistryDelegate(initializer)

class LifeCycleRegistryDelegate<T : LifecycleObserver>(private val initializer: () -> T) {

    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val v = value
        if (v != null) {
            return v
        }
        require(thisRef != null && thisRef is LifecycleOwner)
        val newV = initializer()
        thisRef.lifecycle.addObserver(newV)
        value = newV
        return newV
    }
}