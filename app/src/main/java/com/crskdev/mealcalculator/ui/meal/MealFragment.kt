package com.crskdev.mealcalculator.ui.meal

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
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import com.crskdev.mealcalculator.utils.showSimpleToast
import kotlinx.android.synthetic.main.fragment_meal.*

class MealFragment : DiFragment() {

    private val viewModel by lazy {
        di.mealViewModel()
    }

    private val selectedFoodViewModel by lazy {
        di.selectedFoodViewModel()
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
            adapter = MealEntriesAdapter(LayoutInflater.from(context)) {
                when (it) {
                    is MealEntryAction.EditEntry -> viewModel.editEntry(it.mealEntry)
                    is MealEntryAction.RemoveEntry -> viewModel.removeEntry(it.mealEntry)
                    is MealEntryAction.FoodAction.Edit -> findNavController().navigate(
                        MealFragmentDirections
                            .actionMealFragmentToUpsertFoodFragment(null, it.food.id)
                    )
                    is MealEntryAction.FoodAction.Delete -> {
                        viewModel.deleteFood(it.food)
                    }
                }
            }
        }
        with(toolbarMeal) {
            inflateMenu(R.menu.menu_meal)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_menu_meal_add_food -> {
                        findNavController()
                            .navigate(
                                MealFragmentDirections.ActionMealFragmentToFindFoodFragment()
                                    .setPopOnSelectItem(true)
                            )
                    }
                    R.id.action_menu_meal_save -> {
                        viewModel.save()
                    }
                }
                true
            }
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            Unit
        }
        viewModel.mealEntriesLiveData.observe(this, Observer {
            recyclerMealEntries.adapter?.cast<MealEntriesAdapter>()?.apply {
                submitList(it)
            }
            if(it.isNotEmpty()){
                view.postDelayed(200) {
                    recyclerMealEntries.scrollToPosition(0)
                }
            }
        })
        viewModel.mealSummaryLiveData.observe(this, Observer {
            toolbarMeal.title = "No.${it.numberOfTheDay}"
            val summary =
                """Calories: ${it.calories.toString().format(2)} kCal.
Carbohydrates: ${it.carbohydrate.total.toString().format(2)} g
Fat: ${it.fat.total.toString().format(2)} g
Proteins: ${it.protein.toString().format(2)} g
Glycemic Load: ${it.glycemicLoad.toString().format(2)}
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
        selectedFoodViewModel.selectedFoodLiveData.observe(this, Observer {
            viewModel.addFood(it)
        })
    }
}
