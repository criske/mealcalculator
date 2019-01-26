package com.crskdev.mealcalculator.presentation.common.livedata

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Cristian Pela on 26.01.2019.
 */
class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val mPending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }
        // Observe the internal MutableLiveData
        super.observe(owner, Observer { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    fun toNonSingleLiveData(): LiveData<T> {
        val liveData = MediatorLiveData<T>()
        liveData.addSource(this) { t -> liveData.value = t }
        return liveData
    }

    companion object {
        private const val TAG = "SingleLiveEvent"
    }
}
