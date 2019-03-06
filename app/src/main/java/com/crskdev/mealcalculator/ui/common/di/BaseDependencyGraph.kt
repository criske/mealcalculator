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

    val scopes = mutableMapOf<KClass<*>, Scope>()

    val injected = mutableMapOf<KClass<*>, Any>()

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
                    scopes[activity::class]?.cast<ScopeImpl>()?.clear()
                    scopes.remove(activity::class)
                    injected.remove(activity::class)
                }

                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    if (activity is AppCompatActivity) {
                        activity.supportFragmentManager
                            .registerFragmentLifecycleCallbacks(
                                object :
                                    FragmentManager.FragmentLifecycleCallbacks() {
                                    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                                        scopes[f::class]?.cast<ScopeImpl>()?.clear()
                                        scopes.remove(f::class)
                                        injected.remove(f::class)
                                    }
                                }, true
                            )
                    }
                }
            })
    }

    fun inject(fragment: Fragment) {
        injected[fragment::class] = fragment
        scopes[fragment::class] = ScopeImpl()
    }

    fun inject(activity: AppCompatActivity) {
        injected[activity::class] = activity
        scopes[activity::class] = ScopeImpl()
    }

    protected inline fun <reified F : Fragment> fragment(): F =
        injected.getOrElse(F::class) {
            throw IllegalAccessException("Fragment ${F::class} was not injected")
        } as F

    protected inline fun <reified A : AppCompatActivity> activityProvider(): () -> A? =
        {
            injected[A::class] as A?
        }

    protected inline fun <reified F : Fragment> fragmentProvider(): () -> F? =
        {
            injected[F::class] as F?
        }

    protected inline fun <reified A : AppCompatActivity> activity(): A =
        injected.getOrElse(A::class) {
            throw IllegalAccessException("Activity ${A::class} was not injected")
        } as A


    inline fun <reified T> scoped(crossinline factory: (Scope) -> T): (Scope) -> T = {
        val scopeImpl = it as ScopeImpl
        val instance = scopeImpl.get(T::class)
        if (instance == null) {
            val created = factory(it)
            scopeImpl.add(created as Any)
            created
        } else {
            instance as T
        }
    }

    inline fun <reified T> getScope() = scopes.getOrElse(T::class) {
        throw IllegalAccessException("Scope for ${T::class} was not found")
    }

    fun getScope(fragment: Fragment) = scopes.getOrElse(fragment::class) {
        throw IllegalAccessException("Scope for ${fragment::class} was not found")
    }


    class ScopeImpl internal constructor() : Scope {

        private val instances = mutableListOf<Any>()

        private val onOutOfScopeListeners: MutableList<() -> Unit> = mutableListOf()


        fun add(any: Any) {
            instances.add(any)
        }

        fun get(clazz: KClass<*>) =
            instances.firstOrNull { it::class == clazz }

        fun clear() {
            onOutOfScopeListeners.forEach {
                it.invoke()
            }
            instances.clear()
            onOutOfScopeListeners.clear()
        }

        override fun onOutOfScope(listener: () -> Unit) {
            onOutOfScopeListeners.add(listener)
        }
    }

    interface Scope {

        fun onOutOfScope(listener: () -> Unit)
    }

}

