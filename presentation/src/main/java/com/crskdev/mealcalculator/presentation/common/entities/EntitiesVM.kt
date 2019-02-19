package com.crskdev.mealcalculator.presentation.common.entities

import com.crskdev.mealcalculator.presentation.common.entities.FloatFormat.Companion.ZERO

/**
 * Created by Cristian Pela on 19.02.2019.
 */
data class CarbohydrateVM(val total: FloatFormat, val fiber: FloatFormat, val sugar: FloatFormat)

data class FatVM(val total: FloatFormat, val saturated: FloatFormat, val unsaturated: FloatFormat)


data class FoodVM(val id: Long = 0,
                  val name: String,
                  val picture: String?,
                  val calories: String,
                  val carbohydrates: CarbohydrateVM,
                  val fat: FatVM,
                  val proteins: FloatFormat,
                  val gi: FloatFormat) {
    companion object {
        fun empty(): FoodVM = FoodVM(
            0, "", null, "0",
            CarbohydrateVM(ZERO, ZERO, ZERO),
            FatVM(ZERO, ZERO, ZERO), ZERO, ZERO
        )
    }
}

data class MealVM(val id: Long = 0,
                  val numberOfTheDay: String,
                  val calories: String,
                  val carbohydrate: CarbohydrateVM,
                  val fat: FatVM,
                  val protein: FloatFormat,
                  val glycemicLoad: FloatFormat,
                  val date: String)

data class MealEntryVM(val id: Long,
                       val mealId: Long,
                       val mealDate: String,
                       val mealNumber: String,
                       val quantity: String,
                       val food: FoodVM)

data class RecipeVM(val id: Long, val name: String, val foodNames: String?)
data class RecipeFoodVM(val id: Long = 0L, val food: FoodVM, val quantity: String) {
    data class SummaryVM(val calories: String, val carbohydrates: CarbohydrateVM, val fat: FatVM,
                         val proteins: FloatFormat, val gi: FloatFormat) {
        companion object {
            val EMPTY = RecipeFoodVM.SummaryVM(
                "0", CarbohydrateVM(ZERO, ZERO, ZERO),
                FatVM(ZERO, ZERO, ZERO), ZERO, ZERO
            )
        }
    }
}

data class RecipeDetailedVM(val id: Long, val name: String, val foods: List<RecipeFoodVM>)