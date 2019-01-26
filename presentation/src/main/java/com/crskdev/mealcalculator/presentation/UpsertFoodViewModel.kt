package com.crskdev.mealcalculator.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.utils.cast

/**
 * Created by Cristian Pela on 25.01.2019.
 */
class UpsertFoodViewModel(private val upsertType: UpsertType) : CoroutineScopedViewModel() {

    companion object {
        val ADD_TYPE = UpsertType(0)
        val EDIT_TYPE = UpsertType(1)
        const val FIELD_NAME = 1
        const val FIELD_CALORIES = 2
        const val FIELD_CARBS_TOTAL = 3
        const val FIELD_CARBS_FIBER = 4
        const val FIELD_CARBS_SUGAR = 5
        const val FIELD_FATS_TOTAL = 6
        const val FIELD_FATS_SATURATED = 7
        const val FIELD_FATS_UNSATURATED = 8
        const val FIELD_PROTEINS = 9
        const val FIELD_GI = 10
    }

    sealed class Error {
        sealed class FieldError(vararg val fieldIndices: Int) {
            class Empty(vararg fieldIndices: Int) : FieldError(*fieldIndices)
            class Negative(vararg fieldIndices: Int) : FieldError(*fieldIndices)
            class InvalidName : FieldError(FIELD_NAME)
            class GIOutOfBounds(val value: Int?, val min: Int = 0, val max: Int = 100) : FieldError(
                FIELD_GI)
            class NumberType(vararg fieldIndices: Int) : FieldError(*fieldIndices)
        }

        class Other(throwable: Throwable) : Error()
        class Composite(vararg val errors: Error) : Error()
    }

    private val invalidFieldsLiveData: LiveData<List<Int>> = MutableLiveData<List<Int>>()

    private val retainedModelLiveData: LiveData<FoodVM> = MutableLiveData<FoodVM>()

    fun restore(foodVM: FoodVM) {
        if (retainedModelLiveData.value == null) {
            retainedModelLiveData.cast<MutableLiveData<FoodVM>>().value = foodVM
        }
    }

    fun upsert(foodVM: FoodVM) {

    }


}


data class FoodVM(val id: Long = 0,
                  val name: String,
                  val picturePath: String?,
                  val calories: String,
                  val carbohydrates: CarbohydrateVM,
                  val fat: FatVM,
                  val proteins: String,
                  val gi: String)

data class FatVM(val total: String, val saturated: String, val unsaturated: String)

data class CarbohydrateVM(val total: String, val fiber: String, val sugar: String)


inline class UpsertType(val value: Int)