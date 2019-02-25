package com.crskdev.mealcalculator.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.RecipeDetailed
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.interactors.RecipeLoadInteractor
import com.crskdev.mealcalculator.domain.interactors.RecipeSaveInteractor
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.livedata.mutableSet
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 13.02.2019.
 */
class RecipeUpsertViewModel(
    private val recipeId: Long,
    private val recipeLoadInteractor: RecipeLoadInteractor,
    private val recipeSaveInteractor: RecipeSaveInteractor
) : CoroutineScopedViewModel() {

    val recipeLiveData: LiveData<RecipeDetailed> = MutableLiveData<RecipeDetailed>().apply {
        value = RecipeDetailed.EMPTY
    }

    val actionResponseLiveData: LiveData<RecipeSaveInteractor.Response> =
        MutableLiveData<RecipeSaveInteractor.Response>()

    val savedStateLiveData: LiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = true
    }

    init {
        if (recipeId > 0) {
            launch {
                recipeLoadInteractor.request(recipeId, true, true) {
                    recipeLiveData.mutableSet(it.copy(foods = emptyList()))
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

    fun save(foods: List<RecipeFood>) {
        recipeLiveData.value?.run {
            launch {
                recipeSaveInteractor.request(this@run.copy(foods = foods)) {
                    if (it is RecipeSaveInteractor.Response.OK) {
                        recipeLiveData.value?.run {
                            recipeLiveData.mutableSet(copy(id = it.recipeId))
                        }
                        savedStateLiveData.mutableSet(true)
                    }
                    actionResponseLiveData.mutableSet(it)
                }
            }
        }
    }

}