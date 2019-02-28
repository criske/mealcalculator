package com.crskdev.mealcalculator.ui.recipe


import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.common.EventBusViewModel
import com.crskdev.mealcalculator.presentation.common.toTargetId
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import com.crskdev.mealcalculator.ui.meal.MealFragmentDirections
import com.crskdev.mealcalculator.ui.meal.RecipeFoodEntriesAdapter
import com.crskdev.mealcalculator.ui.meal.RecipeFoodEntryAction
import com.crskdev.mealcalculator.ui.meal.ViewHolderFinder
import com.crskdev.mealcalculator.utils.onItemSwipe
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventBusViewModel.eventLiveData.observe(this, Observer {
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
                RecipeFoodsEventCodes.EDIT_FOOD_TO_RECIPE -> {
                    if (it.code.sourceId.value == parentID) {
                        viewModel.editEntry(it.data.cast())
                    }
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(recyclerRecipeFoodsEntries) {
            val viewHolderFinder = object : ViewHolderFinder {
                override fun scrollToViewHolder(position: Int, onScrolled: (RecyclerView.ViewHolder) -> Unit) {
                    val r = this@with
                    val listener = object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            val thisListener = this
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                r.removeOnScrollListener(thisListener)
                                r.findViewHolderForLayoutPosition(position)?.also {
                                    onScrolled(it)
                                }
                            }
                        }
                    }
                    r.addOnScrollListener(listener)
                    r.scrollToPosition(position)
                }
            }
            GravitySnapHelper(Gravity.BOTTOM).attachToRecyclerView(this)
            adapter = RecipeFoodEntriesAdapter(LayoutInflater.from(context), viewHolderFinder) {
                when (it) {
                    is RecipeFoodEntryAction.EditEntry -> viewModel.editEntry(it.recipeFood)
                    is RecipeFoodEntryAction.RemoveEntry -> viewModel.removeEntry(it.recipeFood)
                    is RecipeFoodEntryAction.FoodAction.Edit -> {
                        findNavController().navigate(
                            MealFragmentDirections
                                .actionMealFragmentToUpsertFoodFragment(null, it.food.id)
                        )
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
    }
}
