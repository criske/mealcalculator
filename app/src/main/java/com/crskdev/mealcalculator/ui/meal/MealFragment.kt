package com.crskdev.mealcalculator.ui.meal

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.postDelayed
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.presentation.meal.MealViewModel
import com.crskdev.mealcalculator.ui.common.HasBackPressedAwareness
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import com.crskdev.mealcalculator.utils.*
import kotlinx.android.synthetic.main.fragment_meal.*

class MealFragment : DiFragment(), HasBackPressedAwareness {

    companion object {
        private const val SEARCH_FOOD_SELECT_CODE = 10001
        private const val SEARCH_RECIPE_SELECT_CODE = 10002
    }


    private val viewModel by lazy {
        di.mealViewModel()
    }

    private val selectedFoodViewModel by lazy {
        di.selectedFoodViewModel()
    }

    private val selectedRecipeViewModel by lazy {
        di.selectedRecipeViewModel()
    }

    private val mealConflictDialogHelper by lifecycleRegistry {
        MealConflictDialogCreator(context!!) {
            when (it) {
                ConflictAction.ClearAll -> viewModel.clearConflicts()
                is ConflictAction.Handled -> viewModel.conflictHandledWith(it.recipeFood)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textMealSummary.setOnClickListener {
            findNavController().navigate(
                MealFragmentDirections
                    .actionMealFragmentToMealJournalDetailFragment(-1)
            )
        }
        with(recyclerMealEntries) {
            adapter = RecipeFoodEntriesAdapter(LayoutInflater.from(context)) {
                when (it) {
                    is RecipeFoodEntryAction.EditEntry -> viewModel.editEntry(it.recipeFood)
                    is RecipeFoodEntryAction.RemoveEntry -> viewModel.removeEntry(it.recipeFood)
                    is RecipeFoodEntryAction.FoodAction.Edit -> findNavController().navigate(
                        MealFragmentDirections
                            .actionMealFragmentToUpsertFoodFragment(null, it.food.id)
                    )
                    is RecipeFoodEntryAction.FoodAction.Delete -> {
                        viewModel.deleteFood(it.food)
                    }
                }
            }
            onItemSwipe { vh, _ ->
                viewModel.removeEntryIndex(vh.adapterPosition)
            }
        }
        with(toolbarMeal) {
            inflateMenu(R.menu.menu_meal)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_menu_meal_add_food -> {
                        findNavController()
                            .navigate(
                                MealFragmentDirections
                                    .ActionMealFragmentToFindFoodFragment(SEARCH_FOOD_SELECT_CODE)
                            )
                    }
                    R.id.action_menu_meal_save -> {
                        //when using the toolbar context, the dialog show not as compact as when using fragment context. weird?
                        this@MealFragment.context!!.showSimpleYesNoDialog(
                            "Alert",
                            "Are you sure you want to save the meal?"
                        ) { b ->
                            if (b == DialogInterface.BUTTON_POSITIVE) {
                                viewModel.save()
                            }
                        }
                    }
                    R.id.action_menu_meal_load_from_recipe -> {
                        findNavController()
                            .navigate(
                                MealFragmentDirections.actionMealFragmentToRecipesDisplayFragment(
                                    SEARCH_RECIPE_SELECT_CODE
                                )
                            )
                    }
                    R.id.action_menu_meal_save_as_recipe -> {
                        this@MealFragment.context!!.showSimpleInputDialog("Recipe Name") { e ->
                            viewModel.saveAsRecipe(e.toString())
                        }
                    }
                }
                true
            }
            setNavigationOnClickListener {
                handleBackPressed()
            }
            Unit
        }
        viewModel.mealEntriesLiveData.observe(this, Observer {
            recyclerMealEntries.adapter?.cast<RecipeFoodEntriesAdapter>()?.apply {
                submitList(it)
            }
            if (it.isNotEmpty()) {
                view.postDelayed(400) {
                    recyclerMealEntries.smoothScrollToPosition(0)
                }
            }
        })
        viewModel.mealNumberLiveData.observe(this, Observer {
            toolbarMeal.title = "No.$it"
        })
        viewModel.mealSummaryLiveData.observe(this, Observer {
            //todo vm this
            val summary =
                """
Calories: ${it.calories.toString().format(2)} kCal.
Carbohydrates: ${it.carbohydrates.total.toString().format(2)} g
Fat: ${it.fat.total.toString().format(2)} g
Proteins: ${it.proteins.toString().format(2)} g
Glycemic Load: ${it.gi.toString().format(2)}
                """.trimIndent()

            textMealSummary.text = summary
        })
        viewModel.responsesLiveData.observe(this, Observer {
            when (it) {
                MealViewModel.Response.Saved -> {
                    findNavController().popBackStack()
                }
                is MealViewModel.Response.Error -> {
                    val err = if (it is MealViewModel.Response.Error.Other) {
                        it.throwable.toString()
                    } else {
                        it.toString()
                    }
                    context?.showSimpleToast(err)
                    Log.e("MealFragment", err)
                }
            }
        })

        viewModel.conflictLoadFromRecipeFoods.observe(this, Observer {
            mealConflictDialogHelper.showDialogWith(it)
        })

        selectedFoodViewModel.eventLiveData.observe(this, Observer {
            if (it.code == SEARCH_FOOD_SELECT_CODE)
                viewModel.addFood(it.data)
        })

        selectedRecipeViewModel.eventLiveData.observe(this, Observer {
            if (it.code == SEARCH_RECIPE_SELECT_CODE)
                viewModel.loadEntriesFromRecipe(it.data.id)
        })
    }

    override fun handleBackPressed(): Boolean {
        context!!.showSimpleYesNoDialog("Warning", "Exit meal without saving?") {
            if (DialogInterface.BUTTON_POSITIVE == it) {
                findNavController().popBackStack()
            }
        }
        return true
    }
}
