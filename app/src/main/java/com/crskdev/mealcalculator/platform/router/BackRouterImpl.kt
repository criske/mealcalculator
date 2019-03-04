package com.crskdev.mealcalculator.platform.router

import androidx.fragment.app.Fragment
import com.crskdev.mealcalculator.presentation.common.router.BackRouter

/**
 * Created by Cristian Pela on 04.03.2019.
 */
class BackRouterImpl(fragmentProvider: () -> Fragment?) : PlatformBaseRouter(fragmentProvider),
    BackRouter {

    override fun routeBack() {
        findNavController()?.popBackStack()
    }

}