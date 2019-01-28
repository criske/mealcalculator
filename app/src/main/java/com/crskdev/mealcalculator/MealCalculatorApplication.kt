@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.crskdev.mealcalculator

import android.app.Application
import android.content.Context
import com.crskdev.mealcalculator.data.FoodRepositoryImpl
import com.crskdev.mealcalculator.data.MealRepositoryImpl
import com.crskdev.mealcalculator.data.internal.room.MealCalculatorDatabase
import com.crskdev.mealcalculator.domain.gateway.FoodRepository
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.gateway.MealRepository
import com.crskdev.mealcalculator.domain.interactors.FoodActionInteractor
import com.crskdev.mealcalculator.domain.interactors.FoodActionInteractorImpl
import com.crskdev.mealcalculator.domain.interactors.GetFoodInteractor
import com.crskdev.mealcalculator.domain.interactors.GetFoodInteractorImpl
import com.crskdev.mealcalculator.platform.PictureToStringConverterImpl
import com.crskdev.mealcalculator.platform.PlatformGatewayDispatchers
import com.crskdev.mealcalculator.presentation.common.services.PictureToStringConverter
import com.crskdev.mealcalculator.presentation.common.utils.cast

/**
 * Created by Cristian Pela on 28.01.2019.
 */
class MealCalculatorApplication : Application() {

    lateinit var dependencyGraph: DependencyGraph

    override fun onCreate() {
        super.onCreate()
        dependencyGraph = DependencyGraph(this)
    }
}

fun Context.dependencyGraph() =
    this.applicationContext.cast<MealCalculatorApplication>().dependencyGraph

class DependencyGraph(private val context: Context) {

    val db: MealCalculatorDatabase by lazy {
        MealCalculatorDatabase.inMemory(context)
    }

    val foodRepository: FoodRepository by lazy {
        FoodRepositoryImpl(db)
    }

    val mealRepository: MealRepository by lazy {
        MealRepositoryImpl(db)
    }

    val dispatchers: GatewayDispatchers = PlatformGatewayDispatchers

    ///****************************Interactors*****************

    val pictureToStringConverter: () -> PictureToStringConverter = {
        PictureToStringConverterImpl()
    }

    val getFoodInteractor: () -> GetFoodInteractor = {
        GetFoodInteractorImpl(dispatchers, foodRepository)
    }

    val foodActionInteractor: () -> FoodActionInteractor = {
        FoodActionInteractorImpl(dispatchers, foodRepository)
    }
}