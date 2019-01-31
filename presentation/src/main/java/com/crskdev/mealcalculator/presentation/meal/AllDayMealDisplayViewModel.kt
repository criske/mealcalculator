package com.crskdev.mealcalculator.presentation.meal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.interactors.AllDayMealDisplayInteractor
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.livedata.mutablePost
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by Cristian Pela on 30.01.2019.
 */
class AllDayMealDisplayViewModel(
    private val allDayMealDisplayInteractor: AllDayMealDisplayInteractor
) : CoroutineScopedViewModel() {


    val allDayMealLiveData: LiveData<Meal> = MutableLiveData<Meal>()


    init {
        find()
    }

    fun find() {
        launch {
            allDayMealDisplayInteractor.request(Date()) {
                when (it) {
                    is AllDayMealDisplayInteractor.Response.OK -> {
                        allDayMealLiveData.mutablePost(it.meal)
                    }
                    AllDayMealDisplayInteractor.Response.NotFound -> {
                        //todo
                    }
                }
            }
        }
    }
}