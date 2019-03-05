package com.crskdev.mealcalculator.platform.router

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.common.router.BackRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
open class PlatformBaseRouter(private val fragmentProvider: () -> Fragment?) {

    protected fun findNavController() = fragmentProvider()?.findNavController()?.wrap()


    protected class NavControllerWrapper(private val controller: NavController) {

        fun navigate(@IdRes id: Int, bundle: Bundle? = null) {
            controller.navigate(
                id, bundle, NavOptions.Builder()
                    .setEnterAnim(R.animator.slide_in_left)
                    .setExitAnim(R.animator.slide_out_right)
                    .setPopEnterAnim(R.animator.slide_in_right)
                    .setPopExitAnim(R.animator.slide_out_left)
                    .build()
            )
        }

        fun popBackStack(@IdRes destinationId: Int? = null, inclusive: Boolean = true) {
            if (destinationId == null) {
                controller.popBackStack()
            } else {
                popBackStack(destinationId, inclusive)
            }

        }
    }

    private fun NavController.wrap() = NavControllerWrapper(this)
}

open class PlatformBaseRouterWithBack(fragmentProvider: () -> Fragment?) :
    PlatformBaseRouter(fragmentProvider), BackRouter {

    private val backRouter = BackRouterImpl(fragmentProvider)

    override fun routeBack() {
        backRouter.routeBack()
    }
}