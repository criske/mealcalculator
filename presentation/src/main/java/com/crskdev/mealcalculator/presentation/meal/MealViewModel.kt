package com.crskdev.mealcalculator.presentation.meal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.entities.MealEntry
import com.crskdev.mealcalculator.domain.interactors.CurrentMealActionInteractor
import com.crskdev.mealcalculator.domain.interactors.CurrentMealDisplayInteractor
import com.crskdev.mealcalculator.domain.interactors.CurrentMealEntryDisplayInteractor
import com.crskdev.mealcalculator.domain.interactors.FoodActionInteractor
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.livedata.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Created by Cristian Pela on 29.01.2019.
 */
class MealViewModel(
    private val currentMealDisplayInteractor: CurrentMealDisplayInteractor,
    private val currentMealEntryDisplayInteractor: CurrentMealEntryDisplayInteractor,
    private val mealActionInteractor: CurrentMealActionInteractor,
    private val foodActionInteractor: FoodActionInteractor
) : CoroutineScopedViewModel() {

    val mealEntriesLiveData: LiveData<List<MealEntry>> = MutableLiveData<List<MealEntry>>()

    val mealSummaryLiveData: LiveData<Meal> = MutableLiveData<Meal>()

    val responsesLiveData: LiveData<Response> = SingleLiveEvent<Response>()

    private val mealActionLiveData = MutableLiveData<CurrentMealActionInteractor.Request>().apply {
        value = CurrentMealActionInteractor.Request.StartMeal
    }

    init {
        launch {
            currentMealDisplayInteractor.request {
                mealSummaryLiveData.mutablePost(it)
            }
        }
        launch {
            currentMealEntryDisplayInteractor.request { list ->
                mealEntriesLiveData.mutablePost(list)
                val focusIndex = list.indexOfFirst { it.quantity == 0 }.takeIf { it != -1 }
            }
        }
        launch {
            mealActionLiveData
                .distinctUntilChanged()
                .interval(3000, TimeUnit.MILLISECONDS)
                .toChannel { ch ->
                    mealActionInteractor.request(ch) {
                        when (it) {
                            is CurrentMealActionInteractor.Response.MealStarted -> {
                                mealSummaryLiveData.mutablePost(it.meal)
                            }
                            is CurrentMealActionInteractor.Response.MealSaved -> {
                                responsesLiveData.mutablePost(Response.Saved)
                            }
                            is CurrentMealActionInteractor.Response.Error -> {
                                val err = when (it) {
                                    CurrentMealActionInteractor.Response.Error.NegativeQuantity ->
                                        Response.Error.NegativeOrZeroQuantity
                                    CurrentMealActionInteractor.Response.Error.MealNotStarted ->
                                        Response.Error.MealNotStarted
                                    is CurrentMealActionInteractor.Response.Error.Other ->
                                        Response.Error.Other(it.throwable)
                                    CurrentMealActionInteractor.Response.Error.EmptyMeal ->
                                        Response.Error.EmptyMeal
                                }
                                responsesLiveData.mutablePost(err)
                            }
                        }
                    }
                }
        }
    }

    fun addFood(food: Food) {
        mealSummaryLiveData.value?.let {
            mealActionLiveData.value = CurrentMealActionInteractor.Request.AddEntry(
                MealEntry(0, it.id, it.date, it.numberOfTheDay, 0, food)
            )
        }
    }

    fun removeEntry(entry: MealEntry) {
        mealActionLiveData.value = CurrentMealActionInteractor.Request.RemoveEntry(entry)
    }

    fun editEntry(entry: MealEntry) {
        mealActionLiveData.value = CurrentMealActionInteractor.Request.EditEntry(entry)
    }

    fun deleteFood(food: Food) {
        launch {
            foodActionInteractor.request(FoodActionInteractor.Request.Delete(food)) {
                //no-op
            }
        }
    }

    fun save() {
        mealSummaryLiveData.value?.let {
            mealActionLiveData.value = CurrentMealActionInteractor.Request.SaveMeal(it)
        }

    }

    sealed class Response {
        object Saved : Response()
        sealed class Error : Response() {
            object NegativeOrZeroQuantity : Error()
            object MealNotStarted : Error()
            object EmptyMeal : Error()
            class Other(val throwable: Throwable) : Error()
        }
    }
}