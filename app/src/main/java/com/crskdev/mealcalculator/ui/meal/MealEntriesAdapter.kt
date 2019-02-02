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
import com.crskdev.mealcalculator.domain.entities.MealEntry
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.ui.common.FoodDisplayBindItemDelegate
import com.crskdev.mealcalculator.ui.common.FoodDisplayItemAction
import kotlinx.android.synthetic.main.item_meal_entry.view.*

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

        override fun getChangePayload(oldItem: MealEntry, newItem: MealEntry): Any? {
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
        return getItem(position).id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealEntryVH =
        MealEntryVH(
            inflater.inflate(R.layout.item_meal_entry, parent, false),
            action
        )

    override fun onBindViewHolder(holder: MealEntryVH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: MealEntryVH, position: Int, payloads: MutableList<Any>) {
        val mealEntry = getItem(position)
        if (payloads.isNotEmpty()) {
            holder.bindWithPayload(mealEntry, payloads.first().cast())
        } else {
            holder.bind(mealEntry)
        }
    }

}


class MealEntryVH(itemView: View,
                  private val action: (MealEntryAction) -> Unit) :
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
            with(editMealEntryQuantity) {
                setOnFocusChangeListener { v, hasFocus ->
                    val e = v.cast<EditText>()
                    if (hasFocus) {
                        v.post {
                            e.selectAll()
                        }
                    } else {
                        e.setSelection(0, 0)
                    }
                }
                doAfterTextChanged {
                    mealEntry?.let { m ->
                        val q = editMealEntryQuantity.text
                            ?.trim()
                            ?.takeIf { it.isNotEmpty() }
                            ?.toString()
                            ?.toInt()
                            ?: 0
                        println("Quantity: $q")
                        action(MealEntryAction.EditEntry(m.copy(quantity = q)))
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                clipToOutline = true
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
        bindQuantity(mealEntry.quantity)
        foodDisplayItemDelegate.bind(mealEntry.food)
    }

    fun bindWithPayload(mealEntry: MealEntry, payload: SparseArray<Any>) {
        this.mealEntry = mealEntry
        payload[MealEntriesAdapter.PAYLOAD_QUANTITY]?.cast<Int>()?.also {
            bindQuantity(it)
        }
        payload[MealEntriesAdapter.PAYLOAD_FOOD]?.cast<Food>()?.also {
            foodDisplayItemDelegate.bind(it)
        }
    }

    private fun bindQuantity(quantity: Int) {
        itemView.editMealEntryQuantity.apply {
            if (quantity == 0) {
                requestFocus()
                post {
                    context.getSystemService<InputMethodManager>()
                        ?.showSoftInput(this, InputMethodManager.SHOW_FORCED)
                    action(MealEntryAction.RequestFocus(adapterPosition))
                }
            }
            if (!hasFocus()) {
                setText(quantity.toString())
            }
        }
    }

    fun clear() {
        itemView.editMealEntryQuantity.setText("0")
        foodDisplayItemDelegate.clear()
    }

}

sealed class MealEntryAction {
    class EditEntry(val mealEntry: MealEntry) : MealEntryAction()
    class RemoveEntry(val mealEntry: MealEntry) : MealEntryAction()
    class RequestFocus(val position: Int) : MealEntryAction()
    sealed class FoodAction(val food: Food) : MealEntryAction() {
        class Edit(food: Food) : FoodAction(food)
        class Delete(food: Food) : FoodAction(food)
    }
}