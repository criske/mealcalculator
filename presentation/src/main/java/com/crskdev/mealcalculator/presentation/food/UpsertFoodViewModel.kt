package com.crskdev.mealcalculator.presentation.food

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.gateway.GatewayDispatchers
import com.crskdev.mealcalculator.domain.interactors.FoodActionInteractor
import com.crskdev.mealcalculator.domain.interactors.GetFoodInteractor
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.entities.*
import com.crskdev.mealcalculator.presentation.common.livedata.SingleLiveEvent
import com.crskdev.mealcalculator.presentation.common.livedata.mutablePost
import com.crskdev.mealcalculator.presentation.common.livedata.mutableSet
import com.crskdev.mealcalculator.presentation.common.services.PictureToStringConverter
import com.crskdev.mealcalculator.presentation.food.UpsertFoodViewModel.Error.*
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 25.01.2019.
 */
class UpsertFoodViewModel(
    private val upsertType: UpsertType,
    private val getFoodInteractor: GetFoodInteractor,
    private val foodActionInteractor: FoodActionInteractor,
    private val pictureToStringConverter: PictureToStringConverter,
    private val dispatchers: GatewayDispatchers
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
    }

    sealed class Error {
        sealed class FieldError(vararg val fieldIndices: Int) : Error() {
            class Empty(vararg fieldIndices: Int) : FieldError(*fieldIndices)
            class Negative(vararg fieldIndices: Int) : FieldError(*fieldIndices)
            object InvalidName : FieldError(FIELD_NAME)
            class GIOutOfBounds(val value: Int?, val min: Int = 0, val max: Int = 100) : FieldError(
                FIELD_GI
            )

            class NumberType(vararg fieldIndices: Int) : FieldError(*fieldIndices)
        }

        class FoodNotFound(val id: Long) : Error()
        class Other(throwable: Throwable) : Error()
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


    private val errLiveData: LiveData<Error> = SingleLiveEvent<Error>()

    private val retainedModelLiveData: LiveData<FoodVM> = MutableLiveData<FoodVM>()

    init {

        when (upsertType) {
            is UpsertType.Edit -> {
                launch {
                    getFoodInteractor.request(upsertType.foodId) {
                        when (it) {
                            is GetFoodInteractor.Response.OK -> {
                                retainedModelLiveData.mutablePost(it.food.toVM())
                            }
                            is GetFoodInteractor.Response.NotFound -> {
                                errLiveData.mutablePost(FoodNotFound(it.id))
                            }
                        }
                    }
                }
            }
            is UpsertType.Create -> {
                upsertType.withName?.let {
                    retainedModelLiveData.mutableSet(FoodVM.empty().copy(name = it))
                }

            }
        }
    }

    fun restore(foodVM: FoodVM) {
        if (retainedModelLiveData.value == null) {
            retainedModelLiveData.mutablePost(foodVM)
        }
    }

    fun upsert(foodVM: FoodVM) {
        retainedModelLiveData.value?.let {
            launch {

                val request = if (upsertType is UpsertType.Edit) {
                    FoodActionInteractor.Request.Edit(it.toDomain())
                } else {
                    FoodActionInteractor.Request.Create(it.toDomain())
                }

                foodActionInteractor.request(request) {
                    when (it) {
                        FoodActionInteractor.Response.Edited,
                        is FoodActionInteractor.Response.Created -> {
                            retainedModelLiveData.mutablePost(FoodVM.empty())
                        }
                        is FoodActionInteractor.Response.Error -> {
                            val errCollector = mutableListOf<Error>()
                            handleUpsertError(errCollector, it)
                            errCollector.forEach { errVM ->
                                errLiveData.mutablePost(errVM)
                            }
                        }
                    }
                }
            }
        } ?: throw IllegalStateException("No model is set")
    }

    private fun handleUpsertError(collector: MutableList<Error>, err: FoodActionInteractor.Response.Error) {
        when (err) {
            is FoodActionInteractor.Response.Error.EmptyFields ->
                collector.add(FieldError.Empty(*err.fieldIndices))
            is FoodActionInteractor.Response.Error.NegativeFields ->
                collector.add(FieldError.Negative(*err.fieldIndices))
            is FoodActionInteractor.Response.Error.GIOutOfBounds ->
                collector.add(FieldError.GIOutOfBounds(err.value, err.min, err.max))
            FoodActionInteractor.Response.Error.InvalidName ->
                collector.add(FieldError.InvalidName)
            is FoodActionInteractor.Response.Error.Other ->
                collector.add(Other(err.throwable))
            is FoodActionInteractor.Response.Error.Composite ->
                err.errors.forEach {
                    handleUpsertError(collector, it)
                }
        }
    }

    fun setPicture(path: String?) {
        retainedModelLiveData.value?.let {
            if (path == null) {
                retainedModelLiveData.mutableSet(it.copy(picture = null))
            } else {
                launch(dispatchers.DEFAULT) {
                    try {
                        val imgStr = pictureToStringConverter.convert(path)
                        retainedModelLiveData.mutableSet(it.copy(picture = imgStr))
                    } catch (ex: Exception) {
                        errLiveData.mutablePost(Other(ex))
                    }

                }
            }
        } ?: throw IllegalStateException("No model is set")
    }

}




