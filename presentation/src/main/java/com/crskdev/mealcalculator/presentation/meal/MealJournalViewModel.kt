package com.crskdev.mealcalculator.presentation.meal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.interactors.MealJournalDeleteInteractor
import com.crskdev.mealcalculator.domain.interactors.MealJournalDisplayInteractor
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.livedata.mutablePost
import com.crskdev.mealcalculator.presentation.common.livedata.toChannel
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 01.02.2019.
 */
class MealJournalViewModel(
    private val mealJournalDisplayInteractor: MealJournalDisplayInteractor,
    private val mealJournalDeleteInteractor: MealJournalDeleteInteractor
) : CoroutineScopedViewModel() {


    val mealsLiveData: LiveData<List<Meal>> = MutableLiveData<List<Meal>>()

    private val requestLiveData: MutableLiveData<MealJournalDisplayInteractor.Request> =
        MutableLiveData<MealJournalDisplayInteractor.Request>().apply {
            value = MealJournalDisplayInteractor.Request.All
        }

    init {
        launch {
            requestLiveData.toChannel {
                mealJournalDisplayInteractor.request(it) {
                    mealsLiveData.mutablePost(it)
                }
            }
        }
    }


    fun delete(mealsIndex: Int) {
        mealsLiveData.value?.also {
            it.elementAtOrNull(mealsIndex)?.apply {
                launch {
                    mealJournalDeleteInteractor.request(this@apply.id)
                }
            }
        }
    }
}