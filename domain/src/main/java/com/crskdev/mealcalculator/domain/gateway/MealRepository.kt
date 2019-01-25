package com.crskdev.mealcalculator.domain.gateway

import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.entities.MealEntry

/**
 * Created by Cristian Pela on 24.01.2019.
 */
interface MealRepository : Transactionable<MealRepository> {

    fun getAllTodayMeal(): Meal?

    fun saveAllToday(meal: Meal)

    fun getAllTodayMealCount(): Int

    fun getAllTodayMealId(): Long

    fun startAllTodayMeal(meal: Meal)

    fun addCurrentMealEntry(mealEntry: MealEntry)

    fun editCurrentMealEntry(mealEntry: MealEntry)

    fun removeCurrentMealEntry(mealEntry: MealEntry)

    fun existentCurrentMealEntryWithFood(foodId: Long): MealEntry?

    fun discardCurrentMealEntries()

    suspend fun observeCurrentMealEntries(observer: (List<MealEntry>) -> Unit)

    suspend fun observeJournalMeals(observer: (List<Meal>) -> Unit)
}