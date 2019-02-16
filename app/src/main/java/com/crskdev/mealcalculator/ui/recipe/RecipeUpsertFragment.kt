package com.crskdev.mealcalculator.ui.recipe


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.interactors.RecipeSaveInteractor
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.ui.common.HasBackPressedAwareness
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import com.crskdev.mealcalculator.ui.meal.RecipeFoodEntriesAdapter
import com.crskdev.mealcalculator.ui.meal.RecipeFoodEntryAction
import com.crskdev.mealcalculator.utils.hideSoftKeyboard
import com.crskdev.mealcalculator.utils.onItemSwipe
import com.crskdev.mealcalculator.utils.showSimpleToast
import com.crskdev.mealcalculator.utils.showSimpleYesNoDialog
import kotlinx.android.synthetic.main.fragment_recipe_upsert.*

class RecipeUpsertFragment : DiFragment(), HasBackPressedAwareness {


    companion object {
        private const val SELECTED_FOOD_RECIPE_UPSERT_CODE = 17777
    }

    private val selectedFoodViewModel by lazy {
        di.selectedFoodViewModel()
    }

    private val viewModel by lazy {
        di.recipeUpsertViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_upsert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(toolbarRecipeUpsert) {
            inflateMenu(R.menu.menu_add_save)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_menu_add -> {
                        findNavController().navigate(
                            RecipeUpsertFragmentDirections
                                .actionRecipeUpsertFragmentToFindFoodFragment(
                                    SELECTED_FOOD_RECIPE_UPSERT_CODE
                                )
                        )
                    }
                    R.id.action_menu_save -> {
                        activity?.hideSoftKeyboard()
                        viewModel.save()
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
                                findNavController().popBackStack()
                            }
                        }
                } else {
                    findNavController().popBackStack()
                }
            }
        }

        with(recyclerRecipeUpsert) {
            adapter = RecipeFoodEntriesAdapter(LayoutInflater.from(context)) {
                when (it) {
                    is RecipeFoodEntryAction.EditEntry -> viewModel.editEntry(it.recipeFood)
                    is RecipeFoodEntryAction.RemoveEntry -> viewModel.removeEntry(it.recipeFood)
                    is RecipeFoodEntryAction.FoodAction.Edit -> {
                        findNavController().navigate(
                            RecipeUpsertFragmentDirections
                                .actionRecipeUpsertFragmentToUpsertFoodFragment(null, it.food.id)
                        )
                    }
                    is RecipeFoodEntryAction.FoodAction.Delete -> viewModel.deleteFood(it.food)
                }
            }
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            onItemSwipe { vh, _ ->
                viewModel.removeEntryIndex(vh.adapterPosition)
            }
        }

        with(inputRecipeUpsertTitle) {
            doAfterTextChanged { e ->
                e?.toString()?.takeIf { it.isNotEmpty() }?.also { txt ->
                    viewModel.setTitle(txt)
                }
            }
        }


        selectedFoodViewModel.eventLiveData.observe(this, Observer {
            if (it.code == SELECTED_FOOD_RECIPE_UPSERT_CODE) {
                viewModel.addFood(it.data)
            }
        })

        viewModel.recipeLiveData.observe(this, Observer {
            inputRecipeUpsertTitle.apply {
                val hasFocus = hasFocus()
                val isEmpty = (this.text?.toString()?.trim() ?: "").isEmpty()
                if (!hasFocus || (hasFocus && isEmpty)) {
                    setText(it.name)
                }
            }
            recyclerRecipeUpsert.adapter?.cast<RecipeFoodEntriesAdapter>()?.submitList(it.foods)
        })
        viewModel.actionResponseLiveData.observe(this, Observer {
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
                findNavController().popBackStack()
            }
        }
        return true
    }


}
