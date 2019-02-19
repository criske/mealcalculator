package com.crskdev.mealcalculator.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.RecipeDetailed
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.interactors.*
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.entities.RecipeFoodVM
import com.crskdev.mealcalculator.presentation.common.entities.toVM
import com.crskdev.mealcalculator.presentation.common.livedata.mutablePost
import com.crskdev.mealcalculator.presentation.common.livedata.mutableSet
import com.crskdev.mealcalculator.presentation.common.livedata.toChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 13.02.2019.
 */
class RecipeUpsertViewModel(
    private val recipeId: Long,
    private val recipeLoadInteractor: RecipeLoadInteractor,
    private val recipeSaveInteractor: RecipeSaveInteractor,
    private val recipeSummaryInteractor: RecipeSummaryInteractor,
    private val recipeFoodEntriesDisplayInteractor: RecipeFoodEntriesDisplayInteractor,
    private val recipeFoodActionInteractor: RecipeFoodActionInteractor,
    private val foodActionInteractor: FoodActionInteractor
) : CoroutineScopedViewModel() {


    val recipeLiveData: LiveData<RecipeDetailed> = MutableLiveData<RecipeDetailed>().apply {
        value = RecipeDetailed.EMPTY
    }

    val recipeSummaryLiveData: LiveData<RecipeFoodVM.SummaryVM> =
        MutableLiveData<RecipeFoodVM.SummaryVM>().apply {
            value = RecipeFoodVM.SummaryVM.EMPTY
        }

    val actionResponseLiveData: LiveData<RecipeSaveInteractor.Response> =
        MutableLiveData<RecipeSaveInteractor.Response>()

    private val recipeFoodActionLiveData = MutableLiveData<RecipeFoodActionInteractor.Request>()

    val savedStateLiveData: LiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = true
    }

    init {
        if (recipeId > 0) {
            launch {
                recipeLoadInteractor.request(recipeId, true, true) {
                    recipeLiveData.mutablePost(it.copy(foods = emptyList()))
                }
            }
        }

        launch {
            recipeSummaryInteractor.request {
                recipeSummaryLiveData.mutablePost(it.toVM())
            }
        }

        launch {
            delay(100) // hacky way to make sure that recipe name is added
            recipeFoodEntriesDisplayInteractor.request {
                recipeLiveData.value?.run {
                    recipeLiveData.mutablePost(copy(foods = it))
                }
            }
        }
        launch {
            recipeFoodActionLiveData
                .toChannel { ch ->
                    recipeFoodActionInteractor.request(ch) {
                        //todo: handle this
                    }
                }
        }
    }


    fun setTitle(name: String) {
        recipeLiveData.value?.run {
            val trimmedName = name.trim()
            if (trimmedName != this.name && trimmedName.isNotEmpty()) {
                savedStateLiveData.mutableSet(false)
                recipeLiveData.mutableSet(copy(name = trimmedName))
            }
        }
    }

    fun save() {
        recipeLiveData.value?.run {
            launch {
                recipeSaveInteractor.request(this@run) {
                    if (it is RecipeSaveInteractor.Response.OK) {
                        recipeLiveData.value?.run {
                            recipeLiveData.mutablePost(copy(id = it.recipeId))
                        }
                        savedStateLiveData.mutablePost(true)
                    }
                    actionResponseLiveData.mutablePost(it)
                }
            }
        }
    }

    fun addFood(food: Food) {
        savedStateLiveData.mutableSet(false)
        recipeFoodActionLiveData.value = RecipeFoodActionInteractor.Request.AddFood(food)
    }

    fun removeEntry(entry: RecipeFood) {
        savedStateLiveData.mutableSet(false)
        recipeFoodActionLiveData.value = RecipeFoodActionInteractor.Request.Remove(entry)
    }

    fun removeEntryIndex(index: Int) {
        recipeLiveData.value?.foods?.elementAtOrNull(index)?.also {
            removeEntry(it)
        }
    }

    fun editEntry(entry: RecipeFood) {
        savedStateLiveData.mutableSet(false)
        recipeFoodActionLiveData.value = RecipeFoodActionInteractor.Request.Edit(entry)
    }

    fun deleteFood(food: Food) {
        savedStateLiveData.mutableSet(false)
        launch {
            foodActionInteractor.request(FoodActionInteractor.Request.Delete(food)) {
                //no-op
            }
        }
    }


}