package com.crskdev.mealcalculator.presentation.meal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.interactors.MealJournalDetailInteractor
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.livedata.mutableSet
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 30.01.2019.
 */
class MealJournalDetailViewModel(
    private val mealId: Long,
    private val mealJournalDetailInteractor: MealJournalDetailInteractor
) : CoroutineScopedViewModel() {


    val allDayMealLiveData: LiveData<Meal> = MutableLiveData<Meal>()


    init {
        find()
    }

    fun find() {
        launch {
            mealJournalDetailInteractor.request(mealId) {
                when (it) {
                    is MealJournalDetailInteractor.Response.OK -> {
                        allDayMealLiveData.mutableSet(it.meal)
                    }
                    MealJournalDetailInteractor.Response.NotFound -> {
                        //todo
                    }
                }
            }
        }
    }
}