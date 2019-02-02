package com.crskdev.mealcalculator.ui.meal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchUIUtil
import androidx.recyclerview.widget.RecyclerView
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import kotlinx.android.synthetic.main.fragment_meal_journal.*

class MealJournalFragment : DiFragment() {

    private val viewModel by lazy {
        di.mealJournalViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal_journal, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(recyclerMealJournal) {
            adapter = MealJournalAdapter(LayoutInflater.from(context)) {
                findNavController().navigate(
                    MealJournalFragmentDirections
                        .actionMealJournalFragmentToMealJournalDetailFragment(it)
                )
            }
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT, ItemTouchHelper.LEFT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    viewModel.delete(viewHolder.adapterPosition)
                }
            }).attachToRecyclerView(this)
            Unit
        }
        viewModel.mealsLiveData.observe(this, Observer {
            require(it is PagedList<Meal>)
            recyclerMealJournal.adapter?.cast<MealJournalAdapter>()?.submitList(it)
        })
    }
}
