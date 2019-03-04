package com.crskdev.mealcalculator.platform.router

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.crskdev.mealcalculator.presentation.common.router.BackRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
open class PlatformBaseRouter(private val fragmentProvider: () -> Fragment?) {

    protected fun findNavController() = fragmentProvider()?.findNavController()
}

open class PlatformBaseRouterWithBack(fragmentProvider: () -> Fragment?) :
    PlatformBaseRouter(fragmentProvider), BackRouter {

    private val backRouter = BackRouterImpl(fragmentProvider)

    override fun routeBack() {
        backRouter.routeBack()
    }
}