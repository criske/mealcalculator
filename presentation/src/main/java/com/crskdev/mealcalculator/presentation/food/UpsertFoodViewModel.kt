package com.crskdev.mealcalculator.presentation.food

import androidx.collection.SparseArrayCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.interactors.FoodActionInteractor
import com.crskdev.mealcalculator.domain.interactors.GetFoodInteractor
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.entities.FoodVM
import com.crskdev.mealcalculator.presentation.common.entities.toDomainUnchecked
import com.crskdev.mealcalculator.presentation.common.entities.toVM
import com.crskdev.mealcalculator.presentation.common.livedata.SingleLiveEvent
import com.crskdev.mealcalculator.presentation.common.livedata.mutableSet
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel.Error.FieldError
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 25.01.2019.
 */
class UpsertFoodViewModel(
    private val upsertType: UpsertType,
    private val getFoodInteractor: GetFoodInteractor,
    private val foodActionInteractor: FoodActionInteractor
) : CoroutineScopedViewModel() {

    companion object {
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
        const val FIELD_NONE = Int.MAX_VALUE
    }

    sealed class Error {
        sealed class FieldError : Error() {
            object Empty : FieldError()
            object Negative : FieldError()
            object InvalidName : FieldError()
            class GIOutOfBounds(val value: Int?, val min: Int = 0, val max: Int = 100) :
                FieldError()

            object NumberType : FieldError()
        }

        class FoodNotFound(val id: Long) : Error()
        class Other(val throwable: Throwable) : Error()
    }

    sealed class UpsertType {
        class Edit(val foodId: Long) : UpsertType()
        class Create(val withName: String?) : UpsertType()
        companion object {
            fun decide(id: Long?, name: String?): UpsertType = id?.let {
                UpsertType.Edit(it)
            } ?: UpsertType.Create(name)
        }
    }


    val errLiveData: LiveData<SparseArrayCompat<MutableList<Error>>> =
        SingleLiveEvent<SparseArrayCompat<MutableList<Error>>>()

    val clearErrorsLiveData: LiveData<Unit> =
        SingleLiveEvent<Unit>()

    val retainedModelLiveData: LiveData<FoodVM> = MutableLiveData<FoodVM>().apply {
        value = FoodVM.empty()
    }

    init {
        when (upsertType) {
            is UpsertType.Edit -> {
                launch {
                    getFoodInteractor.request(upsertType.foodId) {
                        when (it) {
                            is GetFoodInteractor.Response.OK -> {
                                retainedModelLiveData.mutableSet(it.food.toVM())
                            }
                            is GetFoodInteractor.Response.NotFound -> {
                                errLiveData.mutableSet(ErrorCollector().apply {
                                    addErrorToCollector(this, FIELD_NONE, Error.FoodNotFound(it.id))
                                })
                            }
                        }
                    }
                }
            }
            is UpsertType.Create -> {
                upsertType.withName?.also {
                    retainedModelLiveData.mutableSet(FoodVM.empty().copy(name = it))
                }

            }
        }
    }

    fun restore(foodVM: FoodVM) {
        if (retainedModelLiveData.value == null) {
            retainedModelLiveData.mutableSet(foodVM)
        }
    }

    fun upsertWithOutSave(foodVM: FoodVM) {
        retainedModelLiveData.mutableSet(foodVM)
    }

    fun upsert(foodVM: FoodVM) {
        retainedModelLiveData.value?.let {
            launch {
                val request = if (upsertType is UpsertType.Edit) {
                    FoodActionInteractor.Request.Edit(
                        foodVM.copy(
                            id = upsertType.foodId,
                            picture = it.picture
                        ).toDomainUnchecked()
                    )
                } else {
                    FoodActionInteractor.Request.Create(foodVM.copy(picture = it.picture).toDomainUnchecked())
                }
                foodActionInteractor.request(request) {
                    clearErrorsLiveData.mutableSet(Unit)
                    when (it) {
                        is FoodActionInteractor.Response.Created -> {
                            retainedModelLiveData.mutableSet(FoodVM.empty())
                        }
                        is FoodActionInteractor.Response.Edited -> {
                            retainedModelLiveData.mutableSet(it.food.toVM())
                        }
                        is FoodActionInteractor.Response.Error -> {
                            val errCollector = ErrorCollector()
                            handleUpsertError(errCollector, it)
                            errLiveData.mutableSet(errCollector)
                        }
                    }
                }
            }
        } ?: throw IllegalStateException("No model is set")
    }

    private fun handleUpsertError(collector: ErrorCollector, err: FoodActionInteractor.Response.Error) {
        when (err) {
            is FoodActionInteractor.Response.Error.EmptyFields ->
                err.fieldIndices.forEach {
                    addErrorToCollector(collector, it, Error.FieldError.Empty)
                }
            is FoodActionInteractor.Response.Error.InvalidNumberType -> {
                err.fieldIndices.forEach {
                    addErrorToCollector(collector, it, Error.FieldError.NumberType)
                }
            }
            is FoodActionInteractor.Response.Error.NegativeFields ->
                err.fieldIndices.forEach {
                    addErrorToCollector(collector, it, Error.FieldError.Negative)
                }
            is FoodActionInteractor.Response.Error.GIOutOfBounds ->
                addErrorToCollector(
                    collector,
                    FIELD_GI,
                    FieldError.GIOutOfBounds(err.value, err.min, err.max)
                )
            FoodActionInteractor.Response.Error.InvalidName ->
                addErrorToCollector(collector, FIELD_NAME, FieldError.InvalidName)
            is FoodActionInteractor.Response.Error.Other ->
                addErrorToCollector(collector, FIELD_NONE, Error.Other(err.throwable))
            is FoodActionInteractor.Response.Error.Composite ->
                err.errors.forEach {
                    handleUpsertError(collector, it)
                }
        }
    }

    private fun addErrorToCollector(collector: ErrorCollector, field: Int, error: Error) {
        val errors = collector.get(field, mutableListOf())
        if (errors.isEmpty()) {
            collector.put(field, errors)
        }
        errors.add(error)
    }

}

typealias ErrorCollector = SparseArrayCompat<MutableList<UpsertFoodViewModel.Error>>



