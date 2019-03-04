package com.crskdev.mealcalculator.ui.recipe


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.interactors.RecipeSaveInteractor
import com.crskdev.mealcalculator.presentation.common.EventBusViewModel
import com.crskdev.mealcalculator.presentation.common.asSourceID
import com.crskdev.mealcalculator.presentation.common.asTargetID
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.presentation.recipe.RecipeUpsertViewModel
import com.crskdev.mealcalculator.ui.common.HasBackPressedAwareness
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import com.crskdev.mealcalculator.utils.hideSoftKeyboard
import com.crskdev.mealcalculator.utils.showSimpleToast
import com.crskdev.mealcalculator.utils.showSimpleYesNoDialog
import kotlinx.android.synthetic.main.fragment_recipe_upsert.*

class RecipeUpsertFragment : DiFragment(), HasBackPressedAwareness {


    companion object {
        private const val SELECTED_FOOD_RECIPE_UPSERT_CODE = 17777
        private const val GET_RECIPE_FOODS_FOR_SAVE_CODE = 17778
    }

    private val eventBusViewModel by lazy {
        di.eventBusViewModel()
    }

    private val viewModel: RecipeUpsertViewModel by lazy {
        di.recipeUpsertViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_upsert, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventBusViewModel.eventLiveData.observe(this, Observer {
            if (it.code.targetId.value != id) {
                return@Observer
            }
            when (it.code.targetSubId.value) {
                SELECTED_FOOD_RECIPE_UPSERT_CODE -> {
                    eventBusViewModel
                        .sendEvent(
                            EventBusViewModel.Event(
                                EventBusViewModel.Code(
                                    RecipeFoodsEventCodes.ADD_FOOD_TO_RECIPE.asTargetID(),
                                    sourceId = id.asSourceID()
                                ), it.data.cast()
                            )
                        )
                }
                GET_RECIPE_FOODS_FOR_SAVE_CODE -> {
                    viewModel.save(it.data.cast())
                }
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(toolbarRecipeUpsert) {
            inflateMenu(R.menu.menu_add_save)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_menu_add -> {
                        viewModel.routeToFindFood(
                            this@RecipeUpsertFragment.id.asSourceID(),
                            SELECTED_FOOD_RECIPE_UPSERT_CODE.asSourceID()
                        )
                    }
                    R.id.action_menu_save -> {
                        activity?.hideSoftKeyboard()
                        eventBusViewModel
                            .sendEvent(
                                EventBusViewModel.Event(
                                    EventBusViewModel.Code(
                                        RecipeFoodsEventCodes.GET_RECIPE_FOODS_CODE.asTargetID(),
                                        sourceId = this@RecipeUpsertFragment.id.asSourceID(),
                                        sourceSubId = GET_RECIPE_FOODS_FOR_SAVE_CODE.asSourceID()
                                    ), Unit
                                )
                            )
                    }
                }
                true
            }
            setNavigationOnClickListener {
                if (viewModel.savedStateLiveData.value == false) {
                    this@RecipeUpsertFragment
                        .context
                        ?.showSimpleYesNoDialog("Alert", "Leave without saving?") {
                            if (it == DialogInterface.BUTTON_POSITIVE) {
                                viewModel.routeBack()
                            }
                        }
                } else {
                    viewModel.routeBack()
                }
            }
        }

        with(inputRecipeUpsertTitle) {
            doAfterTextChanged { e ->
                e?.toString()?.takeIf { it.isNotEmpty() }?.also { txt ->
                    viewModel.setTitle(txt)
                }
            }
        }
        viewModel.recipeLiveData.observe(viewLifecycleOwner, Observer {
            inputRecipeUpsertTitle.apply {
                val hasFocus = hasFocus()
                val isEmpty = (this.text?.toString()?.trim() ?: "").isEmpty()
                if (!hasFocus || (hasFocus && isEmpty)) {
                    setText(it.name)
                }
            }
        })
        viewModel.actionResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RecipeSaveInteractor.Response.OK -> context?.showSimpleToast("Recipe Saved")
                RecipeSaveInteractor.Response.EmptyName -> context?.showSimpleToast(it.javaClass.simpleName)
                RecipeSaveInteractor.Response.EmptyRecipe -> context?.showSimpleToast(it.javaClass.simpleName)
            }
        })
    }

    override fun handleBackPressed(): Boolean {
        if (viewModel.savedStateLiveData.value == true) {
            return false
        }
        context?.showSimpleYesNoDialog("Alert", "Leave without saving?") {
            if (it == DialogInterface.BUTTON_POSITIVE) {
                viewModel.routeBack()
            }
        }
        return true
    }


}
