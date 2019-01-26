package com.crskdev.mealcalculator.presentation.food

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.interactors.FindFoodInteractor
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.livedata.mutablePost
import com.crskdev.mealcalculator.presentation.common.livedata.toChannel
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 26.01.2019.
 */
class FindFoodViewModel(
    private val findFoodInteractor: FindFoodInteractor
) : CoroutineScopedViewModel() {

    private val searchLiveData = MutableLiveData<String>().apply {
        value = ""
    }

    val foodsLiveData: LiveData<List<Food>> = MutableLiveData<List<Food>>()

    init {
        launch {
            searchLiveData.toChannel {
                findFoodInteractor.request(it) {
                    when (it) {
                        is FindFoodInteractor.Response.FoundList -> foodsLiveData.mutablePost(it.list)
                        is FindFoodInteractor.Response.CreateNewFoodWithQueryNameWhenEmpty -> TODO()
                    }
                }
            }
        }
    }

    fun search(query: String) {
        searchLiveData.value = query
    }

}