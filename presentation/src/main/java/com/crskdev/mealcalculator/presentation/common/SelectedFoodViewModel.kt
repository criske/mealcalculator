package com.crskdev.mealcalculator.presentation.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.presentation.common.livedata.mutableSet

/**
 * Created by Cristian Pela on 29.01.2019.
 */
class SelectedFoodViewModel : ViewModel() {

    val selectedFoodLiveData: LiveData<Food> = MutableLiveData<Food>()

    fun selectFood(food: Food) {
        selectedFoodLiveData.mutableSet(food)
    }

}