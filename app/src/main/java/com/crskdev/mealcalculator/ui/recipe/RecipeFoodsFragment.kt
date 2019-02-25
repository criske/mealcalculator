package com.crskdev.mealcalculator.ui.recipe


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearSmoothScroller
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.common.EventBusViewModel
import com.crskdev.mealcalculator.presentation.common.toTargetId
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import com.crskdev.mealcalculator.ui.meal.RecipeFoodEntriesAdapter
import com.crskdev.mealcalculator.ui.meal.RecipeFoodEntryAction
import com.crskdev.mealcalculator.utils.onItemSwipe
import kotlinx.android.synthetic.main.fragment_recipe_foods.*

class RecipeFoodsFragment : DiFragment() {

    private val viewModel: RecipeFoodsViewModel by lazy {
        di.recipeFoodsViewModel()
    }

    private val eventBusViewModel by lazy {
        di.eventBusViewModel()
    }

    private val parentID: Int by lazy {
        parentFragment?.id ?: EventBusViewModel.Event.NO_CODE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_foods, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = LinearSmoothScroller.SNAP_TO_START
        }
        with(recyclerRecipeFoodsEntries) {
            adapter = RecipeFoodEntriesAdapter(LayoutInflater.from(context)) {
                when (it) {
                    is RecipeFoodEntryAction.EditEntry -> viewModel.editEntry(it.recipeFood)
                    is RecipeFoodEntryAction.RemoveEntry -> viewModel.removeEntry(it.recipeFood)
                    is RecipeFoodEntryAction.FoodAction.Edit -> {
                        //todo use generic navigation or not?
//                        findNavController().navigate(MealFragmentDirections
//                            .actionMealFragmentToUpsertFoodFragment(null, it.food.id)
                        //)
                    }
                    is RecipeFoodEntryAction.FoodAction.Delete -> {
                        viewModel.deleteFood(it.food)
                    }
                }
            }
            onItemSwipe { vh, _ ->
                viewModel.removeEntryIndex(vh.adapterPosition)
            }
        }
        viewModel.mealEntriesLiveData.observe(viewLifecycleOwner, Observer {
            recyclerRecipeFoodsEntries.adapter?.cast<RecipeFoodEntriesAdapter>()?.apply {
                submitList(it)
            }
        })
        viewModel.mealSummaryLiveData.observe(viewLifecycleOwner, Observer {
            textRecipeFoodsSummary.bind(it)
        })

        eventBusViewModel.eventLiveData.observe(viewLifecycleOwner, Observer {
            when (it.code.targetId.value) {
                RecipeFoodsEventCodes.GET_RECIPE_FOODS_CODE -> {
                    val returningTargetId = it.code.sourceId.toTargetId()
                    if (returningTargetId.value != EventBusViewModel.Event.NO_CODE) {
                        val foods = viewModel.mealEntriesLiveData.value ?: emptyList()
                        val returningTargetSubId = it.code.sourceSubId.toTargetId()
                        eventBusViewModel.sendEvent(
                            EventBusViewModel.Event(returningTargetId, returningTargetSubId, foods)
                        )
                    }
                }
                RecipeFoodsEventCodes.ADD_FOOD_TO_RECIPE -> {
                    if (it.code.sourceId.value == parentID) {
                        viewModel.addFood(it.data.cast())
                    }
                }
            }
        })
    }
}
