package com.crskdev.mealcalculator.domain.gateway

import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.entities.MealEntry

/**
 * Created by Cristian Pela on 24.01.2019.
 */
interface MealRepository: Transactionable<MealRepository> {

    fun startCurrentMeal(meal: Meal)

    fun getCurrentMeal(): Meal

    fun discardCurrentMeal()

    fun getTodayMealCount(): Int


    fun addMealEntry(mealEntry: MealEntry)

    fun editMealEntry(mealEntry: MealEntry)

    fun removeMealEntry(mealEntry: MealEntry)

    fun discardCurrentMealEntries()

    fun saveMealToJournal(meal: Meal)

    suspend fun observeCurrentMealEntries(observer: (List<MealEntry>) -> Unit)

}