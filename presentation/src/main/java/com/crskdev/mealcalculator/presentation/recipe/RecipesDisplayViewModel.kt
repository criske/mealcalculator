package com.crskdev.mealcalculator.presentation.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.Recipe
import com.crskdev.mealcalculator.domain.interactors.RecipeDeleteInteractor
import com.crskdev.mealcalculator.domain.interactors.RecipesGetInteractor
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.livedata.SingleLiveEvent
import com.crskdev.mealcalculator.presentation.common.livedata.mutableSet
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 12.02.2019.
 */
class RecipesDisplayViewModel(
    private val router: RecipesDisplayViewModelRouter,
    private val recipesGetInteractor: RecipesGetInteractor,
    private val recipeDeleteInteractor: RecipeDeleteInteractor
) : CoroutineScopedViewModel(), RecipesDisplayViewModelRouter by router {

    val recipesLiveData: LiveData<List<Recipe>> = MutableLiveData<List<Recipe>>()

    val selectedRecipeLiveData: LiveData<Recipe> = SingleLiveEvent<Recipe>()

    init {
        launch {
            recipesGetInteractor.request {
                recipesLiveData.mutableSet(it)
            }
        }
    }

    fun select(recipe: Recipe) {
        selectedRecipeLiveData.mutableSet(recipe)
    }

    fun delete(index: Int) {
        recipesLiveData.value?.also {
            require(index in 0..it.lastIndex)
            launch {
                recipeDeleteInteractor.request(it[index])
            }
        }
    }

}