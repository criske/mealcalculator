package com.crskdev.mealcalculator.ui.meal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.presentation.common.entities.RecipeFoodVM
import com.crskdev.mealcalculator.presentation.common.entities.toVM
import kotlinx.android.synthetic.main.item_meal_journal.view.*

/**
 * Created by Cristian Pela on 01.02.2019.
 */
class MealJournalAdapter(
    private val inflater: LayoutInflater,
    private val action: (Long) -> Unit
) : PagedListAdapter<Meal, MealVH>(
    object : DiffUtil.ItemCallback<Meal>() {
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean =
            oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealVH =
        MealVH(
            inflater.inflate(R.layout.item_meal_journal, parent, false),
            action
        )

    override fun onBindViewHolder(holder: MealVH, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        } ?: holder.clear()
    }

}


class MealVH(v: View, action: (Long) -> Unit) : RecyclerView.ViewHolder(v) {

    private var meal: Meal? = null

    init {
        itemView.setOnClickListener {
            meal?.also {
                action(it.id)
            }
        }
    }

    fun bind(meal: Meal) {
        this.meal = meal
        with(itemView) {
            recipeSummaryMealJournal.bind(meal.summary.toVM())
            textMealJournalDate.text = meal.date
            textMealJournalNumber.text = meal.numberOfTheDay.toString()
        }
    }

    fun clear() {
        print("CLEAR")
        meal = null
        with(itemView) {
            recipeSummaryMealJournal.bind(RecipeFoodVM.SummaryVM.EMPTY)
            textMealJournalDate.text = null
            textMealJournalNumber.text = null
        }

    }
}