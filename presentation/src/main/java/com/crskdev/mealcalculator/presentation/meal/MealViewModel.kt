package com.crskdev.mealcalculator.presentation.meal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.RecipeDetailed
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.interactors.CurrentMealLoadFromRecipeInteractor
import com.crskdev.mealcalculator.domain.interactors.CurrentMealLoadFromRecipeInteractor.ConflictingRecipeFood
import com.crskdev.mealcalculator.domain.interactors.CurrentMealNumberOfTheDayInteractor
import com.crskdev.mealcalculator.domain.interactors.CurrentMealSaveInteractor
import com.crskdev.mealcalculator.domain.interactors.RecipeSaveInteractor
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.livedata.SingleLiveEvent
import com.crskdev.mealcalculator.presentation.common.livedata.mutableSet
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 29.01.2019.
 */
class MealViewModel(
    private val mealRouter: MealViewModelRouter,
    private val currentMealNumberOfTheDayInteractor: CurrentMealNumberOfTheDayInteractor,
    private val currentMealSaveInteractor: CurrentMealSaveInteractor,
    private val currentMealLoadFromRecipeInteractor: CurrentMealLoadFromRecipeInteractor,
    private val saveAsRecipeInteractor: RecipeSaveInteractor
) : CoroutineScopedViewModel(), MealViewModelRouter by mealRouter {


    val mealNumberLiveData: LiveData<Int> = MutableLiveData<Int>()
    val responsesLiveData: LiveData<Response> = SingleLiveEvent<Response>()
    val conflictLoadFromRecipeFoods: LiveData<List<ConflictingRecipeFood>> =
        MutableLiveData<List<ConflictingRecipeFood>>().apply {
            value = emptyList()
        }

    val asRecipeToBeSaved: LiveData<List<RecipeFood>> = MutableLiveData<List<RecipeFood>>()

    init {
        launch {
            currentMealNumberOfTheDayInteractor.request {
                mealNumberLiveData.mutableSet(it)
            }
        }

    }

    fun loadEntriesFromRecipe(recipeId: Long) {
        launch {
            currentMealLoadFromRecipeInteractor.request(recipeId) {
                if (it.isNotEmpty()) {
                    conflictLoadFromRecipeFoods.mutableSet(it)
                }
            }
        }
    }

    fun clearConflicts() {
        conflictLoadFromRecipeFoods.mutableSet(emptyList())
    }

    fun conflictHandledWith(recipeFood: RecipeFood) {
        conflictLoadFromRecipeFoods.value?.also { v ->
            conflictLoadFromRecipeFoods.mutableSet(
                v.filter { it.food.id != recipeFood.food.id }
            )
        }
    }

    fun save(foods: List<RecipeFood>) {
        launch {
            currentMealSaveInteractor.request(foods) {
                val response = when (it) {
                    CurrentMealSaveInteractor.Response.OK -> Response.Saved
                    CurrentMealSaveInteractor.Response.NotSaved -> Response.Error.MealNotSaved
                }
                responsesLiveData.mutableSet(response)
            }
        }
    }

    fun saveAsRecipe(name: String, foods: List<RecipeFood>) {
        launch {
            saveAsRecipeInteractor.request(RecipeDetailed.EMPTY.copy(name = name, foods = foods)) {
                asRecipeToBeSaved.mutableSet(emptyList())
            }
        }
    }

    fun pendingSaveAsRecipe(foods: List<RecipeFood>) {
        asRecipeToBeSaved.mutableSet(foods)
    }


    sealed class Response {
        object Saved : Response()
        object SaveAsRecipe : Response()
        sealed class Error : Response() {
            object NegativeOrZeroQuantity : Error()
            object MealNotStarted : Error()
            object EmptyMeal : Error()
            object MealNotSaved : Error()
            object EmptyRecipeName : Error()
            class Other(val throwable: Throwable) : Error()
        }
    }
}