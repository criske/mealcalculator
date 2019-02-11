package com.crskdev.mealcalculator.presentation.meal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.interactors.*
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.livedata.SingleLiveEvent
import com.crskdev.mealcalculator.presentation.common.livedata.mutablePost
import com.crskdev.mealcalculator.presentation.common.livedata.toChannel
import kotlinx.coroutines.launch

/**
 * Created by Cristian Pela on 29.01.2019.
 */
class MealViewModel(
    private val currentMealNumberOfTheDayInteractor: CurrentMealNumberOfTheDayInteractor,
    private val currentMealSaveInteractor: CurrentMealSaveInteractor,
    private val recipeSummaryInteractor: RecipeSummaryInteractor,
    private val currentMealLoadFromRecipeInteractor: CurrentMealLoadFromRecipeInteractor,
    private val recipeFoodActionInteractor: RecipeFoodActionInteractor,
    private val foodActionInteractor: FoodActionInteractor
) : CoroutineScopedViewModel() {

    val mealNumberLiveData: LiveData<Int> = MutableLiveData<Int>()

    val mealEntriesLiveData: LiveData<List<RecipeFood>> = MutableLiveData<List<RecipeFood>>()

    val mealSummaryLiveData: LiveData<RecipeFood.Summary> =
        MutableLiveData<RecipeFood.Summary>().apply {
            value = RecipeFood.Summary.EMPTY
        }

    val responsesLiveData: LiveData<Response> = SingleLiveEvent<Response>()

    private val recipeFoodActionLiveData = MutableLiveData<RecipeFoodActionInteractor.Request>()

    init {
        launch {
            currentMealNumberOfTheDayInteractor.request {
                mealNumberLiveData.mutablePost(it)
            }
        }
        launch {
            recipeSummaryInteractor.request {
                mealSummaryLiveData.mutablePost(it)
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
                    TODO("Handle showing conflicts")
                }
            }
        }
    }

    fun save() {
        mealEntriesLiveData.value?.also {
            launch {
                currentMealSaveInteractor.request(it) {
                    val response = when (it) {
                        CurrentMealSaveInteractor.Response.OK -> Response.Saved
                        CurrentMealSaveInteractor.Response.NotSaved -> Response.Error.MealNotSaved
                    }
                    responsesLiveData.mutablePost(response)
                }
            }
        }
    }


    sealed class Response {
        object Saved : Response()
        sealed class Error : Response() {
            object NegativeOrZeroQuantity : Error()
            object MealNotStarted : Error()
            object EmptyMeal : Error()
            object MealNotSaved : Error()
            class Other(val throwable: Throwable) : Error()
        }
    }
}