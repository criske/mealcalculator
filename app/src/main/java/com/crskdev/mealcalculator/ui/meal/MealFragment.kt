package com.crskdev.mealcalculator.ui.meal

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.entities.Recipe
import com.crskdev.mealcalculator.presentation.common.EventBusViewModel
import com.crskdev.mealcalculator.presentation.common.asSourceID
import com.crskdev.mealcalculator.presentation.common.asTargetID
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.presentation.meal.MealViewModel
import com.crskdev.mealcalculator.ui.common.HasBackPressedAwareness
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import com.crskdev.mealcalculator.ui.recipe.RecipeFoodsEventCodes
import com.crskdev.mealcalculator.utils.*
import kotlinx.android.synthetic.main.fragment_meal.*

class MealFragment : DiFragment(), HasBackPressedAwareness {

    companion object {
        private const val SEARCH_FOOD_SELECT_CODE = 10001
        private const val SEARCH_RECIPE_SELECT_CODE = 10002
        private const val RESPONSE_RECIPE_FOODS_TO_SAVE_CODE = 10003
        private const val RESPONSE_RECIPE_FOODS_TO_SAVE_AS_RECIPE_CODE = 10004
    }


    private val viewModel: MealViewModel by lazy {
        di.mealViewModel()
    }

    private val eventBusViewModel: EventBusViewModel by lazy {
        di.eventBusViewModel()
    }

    private val mealConflictDialogHelper by lifecycleRegistry {
        MealConflictDialogCreator(context!!) {
            when (it) {
                ConflictAction.ClearAll -> viewModel.clearConflicts()
                is ConflictAction.Handled -> {
                    viewModel.conflictHandledWith(it.recipeFood)
                    eventBusViewModel.sendEvent(
                        EventBusViewModel.Event(
                            EventBusViewModel.Code(
                                targetId = RecipeFoodsEventCodes.EDIT_FOOD_TO_RECIPE.asTargetID(),
                                sourceId = this@MealFragment.id.asSourceID()
                            ), it.recipeFood
                        )
                    )
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventBusViewModel.eventLiveData.observe(this, Observer {
            if (it.code.targetId.value != id) {
                return@Observer
            }
            when (it.code.targetSubId.value) {
                SEARCH_FOOD_SELECT_CODE -> eventBusViewModel
                    .sendEvent(
                        EventBusViewModel.Event(
                            EventBusViewModel.Code(
                                RecipeFoodsEventCodes.ADD_FOOD_TO_RECIPE.asTargetID(),
                                sourceId = id.asSourceID()
                            ),
                            it.data.cast()
                        )
                    )
                SEARCH_RECIPE_SELECT_CODE -> viewModel
                    .loadEntriesFromRecipe(it.data.cast<Recipe>().id)
                RESPONSE_RECIPE_FOODS_TO_SAVE_CODE -> {
                    viewModel.save(it.data.cast())
                }
                RESPONSE_RECIPE_FOODS_TO_SAVE_AS_RECIPE_CODE -> {
                    viewModel.pendingSaveAsRecipe(it.data.cast())
                }
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(toolbarMeal) {
            inflateMenu(R.menu.menu_meal)
            setOnMenuItemClickListener {
                activity?.currentFocus?.clearFocus()
                activity?.hideSoftKeyboard()
                when (it.itemId) {
                    R.id.action_menu_meal_add_food -> {
                        viewModel.routeToFindFood(
                            this@MealFragment.id.asSourceID(),
                            SEARCH_FOOD_SELECT_CODE.asSourceID()
                        )
                    }
                    R.id.action_menu_meal_save -> {
                        //when using the toolbar context, the dialog show not as compact as when using fragment context. weird?
                        this@MealFragment.context!!.showSimpleYesNoDialog(
                            "Alert",
                            "Are you sure you want to save the meal?"
                        ) { b ->
                            if (b == DialogInterface.BUTTON_POSITIVE) {
                                eventBusViewModel
                                    .sendEvent(
                                        EventBusViewModel.Event(
                                            EventBusViewModel.Code(
                                                RecipeFoodsEventCodes.GET_RECIPE_FOODS_CODE.asTargetID(),
                                                sourceId = this@MealFragment.id.asSourceID(),
                                                sourceSubId = RESPONSE_RECIPE_FOODS_TO_SAVE_CODE.asSourceID()
                                            ), Unit
                                        )
                                    )
                            }
                        }
                    }
                    R.id.action_menu_meal_load_from_recipe -> {
                        viewModel.routeToRecipesDisplay(
                            this@MealFragment.id.asSourceID(),
                            SEARCH_RECIPE_SELECT_CODE.asSourceID()
                        )
                    }
                    R.id.action_menu_meal_save_as_recipe -> {
                        eventBusViewModel
                            .sendEvent(
                                EventBusViewModel.Event(
                                    EventBusViewModel.Code(
                                        RecipeFoodsEventCodes.GET_RECIPE_FOODS_CODE.asTargetID(),
                                        sourceId = this@MealFragment.id.asSourceID(),
                                        sourceSubId = RESPONSE_RECIPE_FOODS_TO_SAVE_AS_RECIPE_CODE.asSourceID()
                                    ), Unit
                                )
                            )

                    }
                }
                true
            }
            setNavigationOnClickListener {
                handleBackPressed()
            }
            Unit
        }
        viewModel.responsesLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                MealViewModel.Response.Saved -> {
                    viewModel.routeBack()
                }
                is MealViewModel.Response.Error -> {
                    val err = if (it is MealViewModel.Response.Error.Other) {
                        it.throwable.toString()
                    } else {
                        it.toString()
                    }
                    context?.showSimpleToast(err)
                }
            }
        })
        viewModel.conflictLoadFromRecipeFoods.observe(viewLifecycleOwner, Observer {
            mealConflictDialogHelper.showDialogWith(it)
        })

        viewModel.mealNumberLiveData.observe(viewLifecycleOwner, Observer {
            toolbarMeal.title = "No.$it"
        })

        viewModel.asRecipeToBeSaved.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty())
                this@MealFragment.context!!.showSimpleInputDialog("Recipe Name") { e ->
                    if (e.isNotBlank())
                        viewModel.saveAsRecipe(e.toString(), it)
                }
        })
    }


    override fun handleBackPressed(): Boolean {
        activity?.hideSoftKeyboard()
        context!!.showSimpleYesNoDialog("Warning", "Exit meal without saving?") {
            if (DialogInterface.BUTTON_POSITIVE == it) {
                viewModel.routeBack()
            }
        }
        return true
    }

}

