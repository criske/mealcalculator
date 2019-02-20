package com.crskdev.mealcalculator.presentation.meal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.RecipeDetailed
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.interactors.*
import com.crskdev.mealcalculator.domain.interactors.CurrentMealLoadFromRecipeInteractor.ConflictingRecipeFood
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.entities.RecipeFoodVM
import com.crskdev.mealcalculator.presentation.common.entities.toVM
import com.crskdev.mealcalculator.presentation.common.livedata.SingleLiveEvent
import com.crskdev.mealcalculator.presentation.common.livedata.mutableSet
import com.crskdev.mealcalculator.presentation.common.livedata.toChannel
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 29.01.2019.
 */
class MealViewModel(
    private val currentMealNumberOfTheDayInteractor: CurrentMealNumberOfTheDayInteractor,
    private val recipeFoodEntriesDisplayInteractor: RecipeFoodEntriesDisplayInteractor,
    private val currentMealSaveInteractor: CurrentMealSaveInteractor,
    private val currentMealLoadFromRecipeInteractor: CurrentMealLoadFromRecipeInteractor,
    private val recipeSummaryInteractor: RecipeSummaryInteractor,
    private val recipeFoodActionInteractor: RecipeFoodActionInteractor,
    private val foodActionInteractor: FoodActionInteractor,
    private val saveAsRecipeInteractor: RecipeSaveInteractor
) : CoroutineScopedViewModel() {

    //refactor in a separate model
    val mealEntriesLiveData: LiveData<List<RecipeFood>> = MutableLiveData<List<RecipeFood>>()
    val mealSummaryLiveData: LiveData<RecipeFoodVM.SummaryVM> =
        MutableLiveData<RecipeFoodVM.SummaryVM>().apply {
            value = RecipeFoodVM.SummaryVM.EMPTY
        }
    val scrollPositionLiveData: LiveData<Int> = SingleLiveEvent<Int>()
    private val recipeFoodActionLiveData = MutableLiveData<RecipeFoodActionInteractor.Request>()
    //------------

    val mealNumberLiveData: LiveData<Int> = MutableLiveData<Int>()
    val responsesLiveData: LiveData<Response> = SingleLiveEvent<Response>()
    val conflictLoadFromRecipeFoods: LiveData<List<ConflictingRecipeFood>> =
        MutableLiveData<List<ConflictingRecipeFood>>().apply {
            value = emptyList()
        }



    init {
        //refactor in a separate model
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
        //--------------------
        launch {
            currentMealNumberOfTheDayInteractor.request {
                mealNumberLiveData.mutableSet(it)
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

    fun deleteFood(food: Food) {
        launch {
            foodActionInteractor.request(FoodActionInteractor.Request.Delete(food)) {
                //no-op
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
        recipeFoodActionLiveData.value = RecipeFoodActionInteractor.Request.Edit(recipeFood)
    }

    fun save() {
        mealEntriesLiveData.value?.also {
            launch {
                currentMealSaveInteractor.request(it) {
                    val response = when (it) {
                        CurrentMealSaveInteractor.Response.OK -> Response.Saved
                        CurrentMealSaveInteractor.Response.NotSaved -> Response.Error.MealNotSaved
                    }
                    responsesLiveData.mutableSet(response)
                }
            }
        }
    }

    fun saveAsRecipe(name: String) {
        mealEntriesLiveData.value?.also {
            launch {
                saveAsRecipeInteractor.request(
                    RecipeDetailed.EMPTY.copy(name = name, foods = it)
                ) {}
            }
        }
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