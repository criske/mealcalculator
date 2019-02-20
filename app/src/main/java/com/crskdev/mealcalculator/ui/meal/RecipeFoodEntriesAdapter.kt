package com.crskdev.mealcalculator.ui.meal

import android.os.Build
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.getSystemService
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.ui.food.FoodDisplayBindItemDelegate
import com.crskdev.mealcalculator.ui.food.FoodDisplayItemAction
import kotlinx.android.synthetic.main.item_food_display.view.*
import kotlinx.android.synthetic.main.item_meal_entry.view.*

/**
 * Created by Cristian Pela on 29.01.2019.
 */
class RecipeFoodEntriesAdapter(
    private val inflater: LayoutInflater,
    private val action: (RecipeFoodEntryAction) -> Unit) :
    ListAdapter<RecipeFood, RecipeFoodEntryVH>(
        object : DiffUtil.ItemCallback<RecipeFood>() {
            override fun areItemsTheSame(oldItem: RecipeFood, newItem: RecipeFood): Boolean =
                oldItem.food.id == newItem.food.id

            override fun areContentsTheSame(oldItem: RecipeFood, newItem: RecipeFood): Boolean =
                oldItem == newItem

            override fun getChangePayload(oldItem: RecipeFood, newItem: RecipeFood): Any? {
                return SparseArray<Any>().apply {
                    if (oldItem.quantity != newItem.quantity) {
                        this.put(PAYLOAD_QUANTITY, newItem.quantity)
                    }
                    if (oldItem.food != newItem.food) {
                        this.put(PAYLOAD_FOOD, newItem.food)
                    }
                }
            }
        }) {

    companion object {
        const val PAYLOAD_QUANTITY = 0
        const val PAYLOAD_FOOD = 1
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).food.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeFoodEntryVH =
        RecipeFoodEntryVH(
            inflater.inflate(R.layout.item_meal_entry, parent, false),
            action
        )

    override fun onBindViewHolder(holder: RecipeFoodEntryVH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: RecipeFoodEntryVH, position: Int, payloads: MutableList<Any>) {
        val mealEntry = getItem(position)
        if (payloads.isNotEmpty()) {
            holder.bindWithPayload(mealEntry, payloads.first().cast())
        } else {
            holder.bind(mealEntry)
        }
    }

    override fun onViewRecycled(holder: RecipeFoodEntryVH) {
        holder.clear()
    }
}


class RecipeFoodEntryVH(itemView: View,
                        private val action: (RecipeFoodEntryAction) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    private var recipeFood: RecipeFood? = null

    private var isRecycled = false

    private val foodDisplayItemDelegate =
        FoodDisplayBindItemDelegate(
            itemView,
            itemView.containerFoodDisplay
        ) {
            when (it) {
                is FoodDisplayItemAction.Edit -> action(
                    RecipeFoodEntryAction.FoodAction.Edit(it.food)
                )
                is FoodDisplayItemAction.Delete -> action(
                    RecipeFoodEntryAction.FoodAction.Delete(it.food)
                )
                else -> {
                }
            }
        }


    init {
        with(itemView) {
            with(editMealEntryQuantity) {
                setOnFocusChangeListener { v, hasFocus ->
                    val e = v.cast<EditText>()
                    if (hasFocus) {
                        v.post {
                            e.selectAll()
                        }
                    } else {
                        e.setSelection(0, 0)
                        context.getSystemService<InputMethodManager>()
                            ?.hideSoftInputFromWindow(this.windowToken, 0)
                    }
                }
                doAfterTextChanged {
                    if (!isRecycled) {
                        recipeFood?.let { m ->
                            editMealEntryQuantity.text
                                ?.trim()
                                ?.let { if (it.toString().isEmpty()) "0" else it.toString() }
                                ?.toInt()
                                ?.takeIf { m.quantity != it }
                                ?.also {
                                    action(RecipeFoodEntryAction.EditEntry(m.copy(quantity = it)))
                                }

                        }
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                clipToOutline = true
            }
        }
    }

    fun bind(recipeFood: RecipeFood) {
        this.recipeFood = recipeFood
        bindQuantity(recipeFood.quantity)
        foodDisplayItemDelegate.bind(recipeFood.food)
    }

    fun bindWithPayload(recipeFood: RecipeFood, payload: SparseArray<Any>) {
        this.recipeFood = recipeFood
        payload[RecipeFoodEntriesAdapter.PAYLOAD_QUANTITY]?.cast<Int>()?.also {
            bindQuantity(it)
        }
        payload[RecipeFoodEntriesAdapter.PAYLOAD_FOOD]?.cast<Food>()?.also {
            foodDisplayItemDelegate.bind(it)
        }
    }

    private fun bindQuantity(quantity: Int) {
        isRecycled = false
        itemView.editMealEntryQuantity.apply {
            if (quantity == 0) {
                requestFocus()
                post {
                    context.getSystemService<InputMethodManager>()
                        ?.showSoftInput(this, InputMethodManager.SHOW_FORCED)
                }
            }
            if (!hasFocus()) {
                setText(quantity.toString())
            }
        }
    }

    fun clear() {
        isRecycled = true
        itemView.editMealEntryQuantity.text = null
        foodDisplayItemDelegate.clear()
    }

}

sealed class RecipeFoodEntryAction {
    class EditEntry(val recipeFood: RecipeFood) : RecipeFoodEntryAction()
    class RemoveEntry(val recipeFood: RecipeFood) : RecipeFoodEntryAction()
    sealed class FoodAction(val food: Food) : RecipeFoodEntryAction() {
        class Edit(food: Food) : FoodAction(food)
        class Delete(food: Food) : FoodAction(food)
    }
}