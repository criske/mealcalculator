package com.crskdev.mealcalculator.ui.meal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.MealEntry
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.ui.common.FoodDisplayBindItemDelegate
import com.crskdev.mealcalculator.ui.common.FoodDisplayItemAction
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.item_meal_entry.view.*
import java.lang.Exception

/**
 * Created by Cristian Pela on 29.01.2019.
 */
class MealEntriesAdapter(
    private val inflater: LayoutInflater,
    private val action: (MealEntryAction) -> Unit) : ListAdapter<MealEntry, MealEntryVH>(
    object : DiffUtil.ItemCallback<MealEntry>() {
        override fun areItemsTheSame(oldItem: MealEntry, newItem: MealEntry): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MealEntry, newItem: MealEntry): Boolean =
            oldItem == newItem

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealEntryVH =
        MealEntryVH(
            inflater.inflate(R.layout.item_meal_entry, parent, false),
            action
        )

    override fun onBindViewHolder(holder: MealEntryVH, position: Int) {
        holder.bind(getItem(position))
    }

}


class MealEntryVH(itemView: View, action: (MealEntryAction) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    private var mealEntry: MealEntry? = null

    private val foodDisplayItemDelegate = FoodDisplayBindItemDelegate(itemView) {
        when (it) {
            is FoodDisplayItemAction.Edit -> action(MealEntryAction.FoodAction.Edit(it.food))
            is FoodDisplayItemAction.Delete -> action(MealEntryAction.FoodAction.Delete(it.food))
            else -> {
            }
        }
    }

    init {
        with(itemView) {
            buttonMealQuantityAdd.setOnClickListener {
                mealEntry?.let { m ->
                    try {
                        val q = editMealEntryQuantity.text?.toString()?.toInt()
                        action(MealEntryAction.EditEntry(m.copy(quantity = q ?: 0)))
                    } catch (ex: Exception) {

                    }
                }
            }
            buttonMealEntryRemove.setOnClickListener {
                mealEntry?.let { m ->
                    action(MealEntryAction.RemoveEntry(m))
                }
            }
        }
    }

    fun bind(mealEntry: MealEntry) {
        this.mealEntry = mealEntry
        with(itemView){
            editMealEntryQuantity.setText(mealEntry.quantity.toString())
        }
        foodDisplayItemDelegate.bind(mealEntry.food)
    }

    fun clear() {
        itemView.editMealEntryQuantity.setText("0")
        foodDisplayItemDelegate.clear()
    }

}

sealed class MealEntryAction {
    class EditEntry(val mealEntry: MealEntry) : MealEntryAction()
    class RemoveEntry(val mealEntry: MealEntry) : MealEntryAction()
    sealed class FoodAction(val food: Food) : MealEntryAction() {
        class Edit(food: Food) : FoodAction(food)
        class Delete(food: Food) : FoodAction(food)
    }
}