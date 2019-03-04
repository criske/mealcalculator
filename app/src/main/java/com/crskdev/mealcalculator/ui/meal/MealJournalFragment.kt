package com.crskdev.mealcalculator.ui.meal

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import com.crskdev.mealcalculator.utils.onItemSwipe
import com.crskdev.mealcalculator.utils.showSimpleYesNoDialog
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
        with(toolbarMealJournal) {
            setNavigationOnClickListener {
                viewModel.routeBack()
            }
        }

        with(recyclerMealJournal) {
            adapter = MealJournalAdapter(LayoutInflater.from(context)) {
                //TODO add direction to detailed recipe entry
            }
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            onItemSwipe { vh, _ ->
                context.showSimpleYesNoDialog(
                    "Warning",
                    "Permanently remove this meal journal entry?"
                ) {
                    if (it == DialogInterface.BUTTON_POSITIVE) {
                        viewModel.delete(vh.adapterPosition)
                    } else {
                        adapter?.notifyItemChanged(vh.adapterPosition)
                    }
                }

            }
            Unit
        }
        viewModel.mealsLiveData.observe(viewLifecycleOwner, Observer {
            require(it is PagedList<Meal>)
            recyclerMealJournal.adapter?.cast<MealJournalAdapter>()?.submitList(it)
        })
    }

}
