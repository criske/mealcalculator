package com.crskdev.mealcalculator.ui.common.di

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.crskdev.mealcalculator.dependencyGraph

/**
 * Created by Cristian Pela on 28.01.2019.
 */
open class DiFragment : Fragment() {

    protected val di by lazy {
        context!!.dependencyGraph()
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        di.inject(this)
    }
}