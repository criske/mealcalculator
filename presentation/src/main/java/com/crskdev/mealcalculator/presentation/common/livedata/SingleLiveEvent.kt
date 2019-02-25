package com.crskdev.mealcalculator.presentation.common.livedata

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Cristian Pela on 26.01.2019.
 */
class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)
    private val observers = mutableSetOf<Observer<in T>>()

    private val internalObserver = Observer<T> { t ->
        if (pending.compareAndSet(true, false)) {
            observers.forEach { observer ->
                observer.onChanged(t)
            }
        }
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        observers.add(observer)
        if (!hasObservers()) {
            super.observe(owner, internalObserver)
        }
    }

    @MainThread
    override fun removeObservers(owner: LifecycleOwner) {
        observers.clear()
        super.removeObservers(owner)
    }

    @MainThread
    override fun removeObserver(observer: Observer<in T>) {
        if (observer == internalObserver) {
            observers.clear()
        } else
            observers.remove(observer)
        super.removeObserver(observer)
    }

    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    @MainThread
    fun call() {
        value = null
    }
}