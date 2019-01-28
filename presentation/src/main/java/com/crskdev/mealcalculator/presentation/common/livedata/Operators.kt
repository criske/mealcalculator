package com.crskdev.mealcalculator.presentation.common.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.TimeUnit

/**
 * Created by Cristian Pela on 28.01.2019.
 */
fun <T> LiveData<T>.interval(duration: Long, unit: TimeUnit): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this, object : Observer<T> {
        var lastTime = 0L
        val intervalMillis = unit.toMillis(duration)

        override fun onChanged(t: T) {
            val now = System.currentTimeMillis()
            val delta = now - lastTime
            if (delta > intervalMillis) {
                mutableLiveData.value = t
                lastTime = now
            }
        }
    })
    return mutableLiveData
}

fun <T> LiveData<T>.interval(itemThreshold: Int): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this, object : Observer<T> {
        var emitted = 0
        override fun onChanged(t: T) {
            if (emitted >= itemThreshold) {
                mutableLiveData.value = t
                emitted = 0
            }
            emitted += 1
        }
    })
    return mutableLiveData
}