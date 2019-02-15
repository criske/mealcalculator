package com.crskdev.mealcalculator.presentation.meal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crskdev.mealcalculator.domain.entities.*
import com.crskdev.mealcalculator.domain.interactors.*
import com.crskdev.mealcalculator.domain.interactors.CurrentMealLoadFromRecipeInteractor.ConflictingRecipeFood
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel
import com.crskdev.mealcalculator.presentation.common.livedata.SingleLiveEvent
import com.crskdev.mealcalculator.presentation.common.livedata.mutablePost
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
    private val saveAsRecipeInteractor: RecipeSaveInteractor,
    private val loadFromRecipeInteractor: CurrentMealLoadFromRecipeInteractor
) : CoroutineScopedViewModel() {

    val mealNumberLiveData: LiveData<Int> = MutableLiveData<Int>()

    val mealEntriesLiveData: LiveData<List<RecipeFood>> = MutableLiveData<List<RecipeFood>>()

    val mealSummaryLiveData: LiveData<RecipeFood.Summary> =
        MutableLiveData<RecipeFood.Summary>().apply {
            value = RecipeFood.Summary.EMPTY
        }

    val responsesLiveData: LiveData<Response> = SingleLiveEvent<Response>()

    val conflictLoadFromRecipeFoods: LiveData<List<ConflictingRecipeFood>> =
        MutableLiveData<List<ConflictingRecipeFood>>().apply {
            value = listOf(
                ConflictingRecipeFood(
                    10, 5,
                    Food(
                        0, "Foo", null, 0,
                        Carbohydrate(0f, 0f, 0f),
                        Fat(0f, 0f, 0f), 0f, 0f
                    )
                ),
                ConflictingRecipeFood(
                    1000, 50,
                    Food(
                        1, "Bar", null, 0,
                        Carbohydrate(0f, 0f, 0f),
                        Fat(0f, 0f, 0f), 0f, 0f
                    )
                )
            )
        }

    private val recipeFoodActionLiveData = MutableLiveData<RecipeFoodActionInteractor.Request>()

    init {
        launch {
            recipeFoodEntriesDisplayInteractor.request {
                mealEntriesLiveData.mutablePost(it)
            }
        }
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
                    conflictLoadFromRecipeFoods.mutablePost(it)
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
        //todo reactivate
        // recipeFoodActionLiveData.value = RecipeFoodActionInteractor.Request.AddRecipe(recipeFood)
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