package com.crskdev.mealcalculator.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.RecipeDetailed
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.interactors.*
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.livedata.mutablePost
import com.crskdev.mealcalculator.presentation.common.livedata.toChannel
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 13.02.2019.
 */
class RecipeUpsertViewModel(
    private val recipeId: Long,
    private val recipeLoadInteractor: RecipeLoadInteractor,
    private val recipeSaveInteractor: RecipeSaveInteractor,
    private val recipeFoodEntriesDisplayInteractor: RecipeFoodEntriesDisplayInteractor,
    private val recipeFoodActionInteractor: RecipeFoodActionInteractor,
    private val foodActionInteractor: FoodActionInteractor
) : CoroutineScopedViewModel() {


    val recipeLiveData: LiveData<RecipeDetailed> = MutableLiveData<RecipeDetailed>().apply {
        value = RecipeDetailed.EMPTY
    }

    val actionResponseLiveData: LiveData<RecipeSaveInteractor.Response> =
        MutableLiveData<RecipeSaveInteractor.Response>()

    private val recipeFoodActionLiveData = MutableLiveData<RecipeFoodActionInteractor.Request>()

    init {
        if (recipeId > 0) {
            launch {
                recipeLoadInteractor.request(recipeId, true) {
                    recipeLiveData.mutablePost(it)
                }
            }
        }

        launch {
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
            if (trimmedName != this.name) {
                recipeLiveData.mutablePost(copy(name = trimmedName))
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
                    }
                    actionResponseLiveData.mutablePost(it)
                }
            }
        }
    }

    fun addFood(food: Food) {
        recipeFoodActionLiveData.value = RecipeFoodActionInteractor.Request.AddFood(food)
    }

    fun removeEntry(entry: RecipeFood) {
        recipeFoodActionLiveData.value = RecipeFoodActionInteractor.Request.Remove(entry)
    }

    fun removeEntryIndex(index: Int) {
        recipeLiveData.value?.foods?.elementAtOrNull(index)?.also {
            removeEntry(it)
        }
    }

    fun editEntry(entry: RecipeFood) {
        recipeFoodActionLiveData.value = RecipeFoodActionInteractor.Request.Edit(entry)
    }

    fun deleteFood(food: Food) {
        launch {
            foodActionInteractor.request(FoodActionInteractor.Request.Delete(food)) {
                //no-op
            }
        }
    }


}