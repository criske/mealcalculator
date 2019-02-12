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

    protected val scopes = mutableMapOf<KClass<*>, Scope>()

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
                    scopes[activity::class]?.clear()
                    scopes.remove(activity::class)
                }

                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    if (activity is AppCompatActivity) {
                        activity.supportFragmentManager
                            .registerFragmentLifecycleCallbacks(
                                object :
                                    FragmentManager.FragmentLifecycleCallbacks() {
                                    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                                        scopes[f::class]?.clear()
                                        scopes.remove(f::class)
                                    }
                                }, true
                            )
                    }
                }
            })
    }

    fun inject(fragment: Fragment) {
        scopes[fragment::class] = Scope(fragment)
    }

    fun inject(activity: AppCompatActivity) {
        scopes[activity::class] = Scope(activity)
    }

    protected inline fun <reified F : Fragment> fragment(): F =
        scopes.getOrElse(F::class) {
            throw IllegalAccessException("Fragment ${F::class} was not injected")
        }.holder!! as F

    protected inline fun <reified A : AppCompatActivity> activity(): A =
        scopes.getOrElse(A::class) {
            throw IllegalAccessException("Activity ${A::class} was not injected")
        }.holder!! as A


//    inline fun <reified F : Fragment, reified T> withinFragmentScope(crossinline factory: () -> T) =
//        {
//            scoped(factory)(scopes[F::class]!!)
//        }
//
//
//    inline fun <reified A : AppCompatActivity, reified T> withinActivityScope(crossinline factory: () -> T) =
//        {
//            scoped(factory)(scopes[A::class]!!)
//        }


    inline fun <reified T> scoped(crossinline factory: (Scope) -> T): (Scope) -> T = {
        val instance = it.get(T::class)
        if (instance == null) {
            val created = factory(it)
            it.add(created as Any)
            created
        } else {
            instance as T
        }
    }

    inline fun <reified T> getScope() = scopes.getOrElse(T::class) {
        throw IllegalAccessException("Scope for ${T::class} was not found")
    }

    class Scope(var holder: Any?) {

        private val instances = mutableListOf<Any>()

        fun add(any: Any) {
            instances.add(any)
        }

        fun get(clazz: KClass<*>) =
            instances.firstOrNull { it::class == clazz }

        fun clear() {
            holder = null
            instances.clear()
        }
    }

}

