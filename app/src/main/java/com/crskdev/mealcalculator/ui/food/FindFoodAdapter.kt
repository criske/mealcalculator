package com.crskdev.mealcalculator.ui.food

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.entities.Food

/**
 * Created by Cristian Pela on 28.01.2019.
 */
class FindFoodAdapter(private val inflater: LayoutInflater,
                      private val action: (FoodDisplayItemAction) -> Unit) :
    PagedListAdapter<Food, FindFoodVH>(
        object : DiffUtil.ItemCallback<Food>() {
            override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean =
                oldItem == newItem
        }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindFoodVH =
        FindFoodVH(
            inflater.inflate(R.layout.item_food_display, parent, false),
            action
        )

    override fun onBindViewHolder(holder: FindFoodVH, position: Int) {
        getItem(position)?.also {
            holder.bind(it)
        } ?: holder.clear()
    }

}


class FindFoodVH(view: View, action: (FoodDisplayItemAction) -> Unit) :
    RecyclerView.ViewHolder(view) {

    private val delegate =
        FoodDisplayBindItemDelegate(itemView, itemView, action)

    fun bind(food: Food) {
        delegate.bind(food)
    }

    fun clear() {
        delegate.clear()
    }
}