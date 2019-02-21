package com.crskdev.mealcalculator.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.interactors.FoodActionInteractor
import com.crskdev.mealcalculator.domain.interactors.RecipeFoodActionInteractor
import com.crskdev.mealcalculator.domain.interactors.RecipeFoodEntriesDisplayInteractor
import com.crskdev.mealcalculator.domain.interactors.RecipeSummaryInteractor
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.entities.RecipeFoodVM
import com.crskdev.mealcalculator.presentation.common.entities.toVM
import com.crskdev.mealcalculator.presentation.common.livedata.SingleLiveEvent
import com.crskdev.mealcalculator.presentation.common.livedata.mutableSet
import com.crskdev.mealcalculator.presentation.common.livedata.toChannel
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 20.02.2019.
 */
class RecipeFoodsViewModel(
    private val recipeSummaryInteractor: RecipeSummaryInteractor,
    private val recipeFoodEntriesDisplayInteractor: RecipeFoodEntriesDisplayInteractor,
    private val recipeFoodActionInteractor: RecipeFoodActionInteractor,
    private val foodActionInteractor: FoodActionInteractor
) : CoroutineScopedViewModel() {

    val mealEntriesLiveData: LiveData<List<RecipeFood>> = MutableLiveData<List<RecipeFood>>()
    val mealSummaryLiveData: LiveData<RecipeFoodVM.SummaryVM> =
        MutableLiveData<RecipeFoodVM.SummaryVM>().apply {
            value = RecipeFoodVM.SummaryVM.EMPTY
        }
    val scrollPositionLiveData: LiveData<Int> = SingleLiveEvent<Int>()

    private val recipeFoodActionLiveData = MutableLiveData<RecipeFoodActionInteractor.Request>()

    init {
        launch {
            recipeFoodEntriesDisplayInteractor.request {
                mealEntriesLiveData.mutableSet(it)
                it.indexOfFirst { it.quantity == 0 }.takeIf { it != -1 }?.also {
                    scrollPositionLiveData.mutableSet(it)
                }
            }
        }
        launch {
            recipeFoodActionLiveData
                //  .interval(300, TimeUnit.MILLISECONDS)
                .toChannel { ch ->
                    recipeFoodActionInteractor.request(ch) {
                        //todo: handle this
                    }
                }
        }
        launch {
            recipeSummaryInteractor.request {
                mealSummaryLiveData.mutableSet(it.toVM())
            }
        }
    }

    fun addFood(food: Food) {
        recipeFoodActionLiveData.value = RecipeFoodActionInteractor.Request.AddFood(food)
    }

    fun deleteFood(food: Food) {
        launch {
            foodActionInteractor.request(FoodActionInteractor.Request.Delete(food)) {
                //no-op
            }
        }
    }

    fun removeEntry(entry: RecipeFood) {
        recipeFoodActionLiveData.value = RecipeFoodActionInteractor.Request.Remove(entry)
    }

    fun removeEntryIndex(index: Int) {
        mealEntriesLiveData.value?.elementAtOrNull(index)?.also {
            removeEntry(it)
        }
    }

    fun editEntry(entry: RecipeFood) {
        mealEntriesLiveData.value?.indexOfFirst { it.food.id == entry.food.id }?.also {
            scrollPositionLiveData.mutableSet(it)
        }
        recipeFoodActionLiveData.value = RecipeFoodActionInteractor.Request.Edit(entry)
    }


}