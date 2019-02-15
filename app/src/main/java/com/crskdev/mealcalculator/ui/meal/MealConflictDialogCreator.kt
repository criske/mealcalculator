package com.crskdev.mealcalculator.ui.meal

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.interactors.CurrentMealLoadFromRecipeInteractor.ConflictingRecipeFood
import com.crskdev.mealcalculator.presentation.common.utils.cast
import kotlinx.android.synthetic.main.dialog_conflict_recipe_foods.view.*
import kotlinx.android.synthetic.main.item_conflicting_recipe_food.view.*

/**
 * Created by Cristian Pela on 15.02.2019.
 */
class MealConflictDialogCreator(
    private val context: Context,
    private val action: (ConflictAction) -> Unit) : LifecycleObserver {

    private val inflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    private val view: View by lazy {
        inflater
            .inflate(R.layout.dialog_conflict_recipe_foods, null, false)
            .apply {
                imageDialogConflictsClose.setOnClickListener {
                    dialog.dismiss()
                    action(ConflictAction.ClearAll)
                }
            }
    }

    private val dialog: Dialog by lazy {
        AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(false)
            .create()
    }

    fun showDialogWith(list: List<ConflictingRecipeFood>) {
        if (list.isEmpty()) {
            dialog.dismiss()
        } else {
            if (!dialog.isShowing) {
                dialog.show()
                setupView(list)
            } else {
                submitAdapterList(list)
            }
        }
    }

    private fun submitAdapterList(list: List<ConflictingRecipeFood>) {
        view.recyclerDialogConflicts.adapter?.cast<ConflictingRecipeFoodsAdapter>()
            ?.submitList(list)
    }

    private fun setupView(list: List<ConflictingRecipeFood>) {
        with(view) {
            with(recyclerDialogConflicts) {
                adapter = ConflictingRecipeFoodsAdapter(inflater, action).apply {
                    post {
                        submitList(list)
                    }
                }
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        dialog.dismiss()
    }

}


sealed class ConflictAction {
    object ClearAll : ConflictAction()
    class Handled(val recipeFood: RecipeFood) : ConflictAction()
}

class ConflictingRecipeFoodsAdapter(private val layoutInflater: LayoutInflater,
                                    private val action: (ConflictAction) -> Unit) :
    ListAdapter<ConflictingRecipeFood, ConflictingRecipeFoodVH>(object :
        DiffUtil.ItemCallback<ConflictingRecipeFood>() {
        override fun areItemsTheSame(oldItem: ConflictingRecipeFood, newItem: ConflictingRecipeFood): Boolean =
            oldItem.food.id == newItem.food.id

        override fun areContentsTheSame(oldItem: ConflictingRecipeFood, newItem: ConflictingRecipeFood): Boolean =
            oldItem == newItem

    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConflictingRecipeFoodVH =
        ConflictingRecipeFoodVH(
            layoutInflater.inflate(R.layout.item_conflicting_recipe_food, parent, false),
            action
        )

    override fun onBindViewHolder(holder: ConflictingRecipeFoodVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class ConflictingRecipeFoodVH(v: View, action: (ConflictAction) -> Unit) :
    RecyclerView.ViewHolder(v) {

    private var conflictingRecipeFood: ConflictingRecipeFood? = null

    init {
        with(itemView) {
            textItemConflictFoodQExist.setOnClickListener {
                conflictingRecipeFood?.let {
                    action(ConflictAction.Handled(RecipeFood(0, it.food, it.existentQuantity)))
                }
            }
            textItemConflictFoodQFromRecipe.setOnClickListener {
                conflictingRecipeFood?.let {
                    action(ConflictAction.Handled(RecipeFood(0, it.food, it.recipeQuantity)))
                }
            }
            textItemConflictFoodCombine.setOnClickListener {
                conflictingRecipeFood?.let {
                    action(
                        ConflictAction.Handled(
                            RecipeFood(
                                0, it.food, it.existentQuantity + it.recipeQuantity
                            )
                        )
                    )
                }
            }
        }
    }

    fun bind(conflictingRecipeFood: ConflictingRecipeFood) {
        this.conflictingRecipeFood = conflictingRecipeFood
        with(itemView) {
            textItemConflictFoodName.text = conflictingRecipeFood.food.name
            textItemConflictFoodQExist.text = conflictingRecipeFood.existentQuantity.toString()
            textItemConflictFoodQFromRecipe.text = conflictingRecipeFood.recipeQuantity.toString()
        }
    }

    fun clear() {
        conflictingRecipeFood = null
        with(itemView) {
            textItemConflictFoodName.text = null
            textItemConflictFoodQExist.text = null
            textItemConflictFoodQFromRecipe.text = null
        }
    }


}