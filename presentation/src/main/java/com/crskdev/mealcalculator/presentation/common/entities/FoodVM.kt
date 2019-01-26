package com.crskdev.mealcalculator.presentation.common.entities

/**
 * Created by Cristian Pela on 26.01.2019.
 */
data class FoodVM(val id: Long = 0,
                  val name: String,
                  val picture: String?,
                  val calories: String,
                  val carbohydrates: CarbohydrateVM,
                  val fat: FatVM,
                  val proteins: String,
                  val gi: String) {
    companion object {
        fun empty(): FoodVM = FoodVM(
            0, "", null, "",
            CarbohydrateVM("", "", ""),
            FatVM("", "", ""), "", ""
        )
    }
}

data class FatVM(val total: String, val saturated: String, val unsaturated: String)

data class CarbohydrateVM(val total: String, val fiber: String, val sugar: String)