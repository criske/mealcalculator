@file:Suppress("MemberVisibilityCanBePrivate")

package com.crskdev.mealcalculator.ui.common.di

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.crskdev.mealcalculator.dependencyGraph

/**
 * Created by Cristian Pela on 28.01.2019.
 */
open class DiActivity: AppCompatActivity() {

    protected val di by lazy { dependencyGraph() }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        di.inject(this)
    }

}