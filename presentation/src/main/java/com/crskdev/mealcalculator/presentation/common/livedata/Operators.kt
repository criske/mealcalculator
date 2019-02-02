package com.crskdev.mealcalculator.presentation.common.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Created by Cristian Pela on 28.01.2019.
 */
fun <T> LiveData<T>.interval(duration: Long, unit: TimeUnit): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this, object : Observer<T> {
        var lastTime = 0L
        val intervalMillis = unit.toMillis(duration)

        @Volatile
        var maybeDanglingItem: T? = null
        var danglingTimer: Timer? = null
        val lock = ReentrantLock()

        var isFirstItem = true

        override fun onChanged(t: T) {
            if (isFirstItem) {
                mutableLiveData.value = t
                isFirstItem = false
            } else {
                lock.withLock {
                    maybeDanglingItem = t
                }
                danglingTimer?.cancel()

                val now = System.currentTimeMillis()
                val delta = now - lastTime
                if (delta > intervalMillis) {
                    mutableLiveData.value = t
                    lastTime = now
                } else {
                    startAndRunDanglingTimer(delta)
                }
            }
        }

        private fun startAndRunDanglingTimer(delay: Long) {
            //start timer to emit dangling item after delta
            danglingTimer = Timer("Live Data Operator Interval Dangling Timer").apply {
                schedule(object : TimerTask() {
                    override fun run() {
                        lock.withLock {
                            maybeDanglingItem?.also {
                                if (mutableLiveData.hasActiveObservers())
                                    mutableLiveData.postValue(it)
                            }
                        }
                    }
                }, delay)
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

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this, object : Observer<T> {
        var lastValue: T? = null
        override fun onChanged(t: T) {
            if (lastValue != t) {
                mutableLiveData.value = t
                lastValue = t
            }
        }
    })
    return mutableLiveData
}

inline fun <T> LiveData<T>.distinctUntilChanged(crossinline predicate: (T, T) -> Boolean): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this, object : Observer<T> {
        var lastValue: T? = null
        override fun onChanged(t: T) {
            val prevT = lastValue
            if (prevT == null || predicate(prevT, t)) {
                mutableLiveData.value = t
                lastValue = t
            }
        }
    })
    return mutableLiveData
}
