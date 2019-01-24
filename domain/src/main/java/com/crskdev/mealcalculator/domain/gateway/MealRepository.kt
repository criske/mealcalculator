package com.crskdev.mealcalculator.domain.gateway

import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.entities.MealEntry

/**
 * Created by Cristian Pela on 24.01.2019.
 */
interface MealRepository {

    fun getTodayMealCount(): Int

    fun addMealEntry(mealEntry: MealEntry)

    fun removeMealEntry()

    fun getCurrentMeal(): List<MealEntry>

    fun discardCurrentMeal()

    fun saveMealToJournal(meal: Meal)

    suspend fun observeCurrentMeal(observer: (List<MealEntry>) -> Unit)

}