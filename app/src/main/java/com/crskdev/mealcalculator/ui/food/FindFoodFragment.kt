package com.crskdev.mealcalculator.ui.food


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.common.EventBusViewModel
import com.crskdev.mealcalculator.presentation.common.asTargetID
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.presentation.food.FindFoodViewModel
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import com.crskdev.mealcalculator.utils.showSimpleToast
import kotlinx.android.synthetic.main.fragment_find_food.*

/**
 * A simple [Fragment] subclass.
 *
 */
class FindFoodFragment : DiFragment() {

    private val viewModel: FindFoodViewModel by lazy {
        di.findFoodViewModel()
    }

    private val eventBusViewModel: EventBusViewModel by lazy {
        di.eventBusViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_food, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(toolbarFoodsSearch) {
            inflateMenu(R.menu.menu_food_find)
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener {
                viewModel.routeBack()
            }
            menu.findItem(R.id.action_menu_find_food_search)
                .apply { expandActionView() }
                .actionView
                .cast<SearchView>()
                .apply {
                    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean = false

                        override fun onQueryTextChange(newText: String?): Boolean {
                            newText?.also {
                                viewModel.search(it)
                            }
                            return true
                        }
                    })
                }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_menu_find_food_new -> {
                        viewModel.routeToUpsertFoodNew()
                    }
                }
                true
            }
        }
        with(recyclerFoodsSearch) {
            adapter = FindFoodAdapter(LayoutInflater.from(context)) {
                when (it) {
                    is FoodDisplayItemAction.Select -> {
                        val args = FindFoodFragmentArgs.fromBundle(arguments!!)
                        if (args.sourceId != EventBusViewModel.Event.NO_CODE) {
                            eventBusViewModel
                                .sendEvent(
                                    EventBusViewModel.Event(
                                        args.sourceId.asTargetID(),
                                        args.sourceSubId.asTargetID(),
                                        it.food
                                    )
                                )
                            viewModel.routeBack()
                        }
                    }
                    is FoodDisplayItemAction.Edit -> viewModel.routeToUpsertFoodEdit(it.food.id)
                    is FoodDisplayItemAction.Delete -> viewModel.delete(it.food)
                }
            }
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        viewModel.foodsLiveData.observe(viewLifecycleOwner, Observer {
            if (it is PagedList) {
                recyclerFoodsSearch.adapter?.cast<FindFoodAdapter>()?.submitList(it)
            } else {
                context?.showSimpleToast("Result list must be a Paged List. Current ${it::class}\"")
            }
        })

    }


}
