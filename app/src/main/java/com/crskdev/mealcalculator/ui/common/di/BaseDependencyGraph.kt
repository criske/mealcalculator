@file:Suppress("unused")

package com.crskdev.mealcalculator.ui.common.di

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.crskdev.mealcalculator.presentation.common.utils.cast
import kotlin.reflect.KClass

/**
 * Created by Cristian Pela on 28.01.2019.
 */
open class BaseDependencyGraph(protected val context: Context) {

    protected val injectedActivities =
        mutableMapOf<KClass<out AppCompatActivity>, AppCompatActivity>()

    protected val activityScopes = mutableMapOf<AppCompatActivity, MutableList<Any>>()

    protected val injectedFragments = mutableMapOf<KClass<out Fragment>, Fragment>()

    protected val fragmentScopes = mutableMapOf<Fragment, MutableList<Any>>()

    init {
        context.cast<Application>()
            .registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

                override fun onActivityPaused(activity: Activity?) = Unit

                override fun onActivityResumed(activity: Activity?) = Unit

                override fun onActivityStarted(activity: Activity?) = Unit

                override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) =
                    Unit

                override fun onActivityStopped(activity: Activity?) = Unit

                override fun onActivityDestroyed(activity: Activity) {
                    injectedActivities.remove(activity::class)
                    activityScopes[activity]?.clear()
                    activityScopes.remove(activity)
                }

                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    if (activity is AppCompatActivity) {
                        activity.supportFragmentManager
                            .registerFragmentLifecycleCallbacks(
                                object :
                                    FragmentManager.FragmentLifecycleCallbacks() {
                                    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                                        injectedFragments.remove(f::class)
                                        fragmentScopes[f]?.clear()
                                        fragmentScopes.remove(f)
                                    }
                                }, true
                            )
                    }
                }
            })
    }

    fun inject(fragment: Fragment) {
        injectedFragments[fragment::class] = fragment
        fragmentScopes[fragment] = mutableListOf<Any>()
    }

    fun inject(activity: AppCompatActivity) {
        injectedActivities[activity::class] = activity
        activityScopes[activity] = mutableListOf<Any>()
    }

    protected inline fun <reified F : Fragment> fragment(): F =
        injectedFragments.getOrElse(F::class) {
            throw IllegalAccessException("Fragment ${F::class} was not injected")
        } as F

    protected inline fun <reified A : AppCompatActivity> activity(): A =
        injectedActivities.getOrElse(A::class) {
            throw IllegalAccessException("Fragment ${A::class} was not injected")
        } as A


    inline fun <reified F : Fragment, reified T> withinFragmentScope(crossinline factory: () -> T) =
        {
            val f = fragment<F>()
            val instances = fragmentScopes[f]!!
            val instance = instances.firstOrNull { it is T}
            if (instance == null) {
                val created = factory()
                instances.add(created as Any)
                created
            } else {
                instance as T
            }
        }

    inline fun <reified A : AppCompatActivity, reified T> withinActivityScope(crossinline factory: () -> T) =
        {
            val a = activity<A>()
            val instances = activityScopes[a]!!
            val instance = instances.firstOrNull { it::class == T::class }
            if (instance == null) {
                val created = factory()
                instances.add(created as Any)
                created
            } else {
                instance as T
            }
        }

}