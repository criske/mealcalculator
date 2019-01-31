package com.crskdev.mealcalculator.ui.common

import android.content.DialogInterface
import android.graphics.PorterDuff
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorFilter
import androidx.core.view.forEach
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.utils.ProjectImageUtils
import com.crskdev.mealcalculator.utils.showSimpleYesNoDialog
import kotlinx.android.synthetic.main.item_food_display.view.*

/**
 * Created by Cristian Pela on 29.01.2019.
 */
class FoodDisplayBindItemDelegate(private val itemView: View, action: (FoodDisplayItemAction) -> Unit) {

    var food: Food? = null

    init {
        with(itemView) {
            setOnClickListener {
                food?.also {
                    action(FoodDisplayItemAction.Select(it))
                }
            }
            setOnCreateContextMenuListener { menu, view, info ->
                with(menu) {
                    MenuInflater(context).inflate(R.menu.menu_conxtextual_food_display_item, this)
                    forEach { item ->
                        item.setOnMenuItemClickListener {
                            when (item.itemId) {
                                R.id.menu_action_food_edit -> {
                                    food?.also { action(FoodDisplayItemAction.Edit(it)) }
                                }
                                R.id.menu_action_food_delete -> {
                                    food?.also { f ->
                                        context.showSimpleYesNoDialog(
                                            context.getString(R.string.warning),
                                            context.getString(
                                                R.string.msg_warning_delete_food,
                                                f.name
                                            )
                                        ) {
                                            if (it == DialogInterface.BUTTON_POSITIVE) {
                                                action(FoodDisplayItemAction.Delete(f))
                                            }
                                        }
                                    }
                                }
                            }
                            true
                        }
                    }
                }

            }
        }
    }

    fun bind(food: Food) {
        this.food = food
        with(itemView) {
            textFoodDisplayName.text = food.name
            textFoodDisplayCalories.text = "${food.calories}kCal."
            textFoodDisplayMacros.text = buildString {
                append("C:${food.carbohydrates.total}g\n")
                append("F:${food.fat.total}g\n")
                append("P:${food.proteins}g")
            }

            val picture = food.picture?.let {
                ProjectImageUtils.convertStrToRoundedDrawable(resources, it)
            } ?: ContextCompat.getDrawable(context, R.drawable.ic_fruit_24dp)?.apply {
//                colorFilter = PorterDuff.Mode.SRC_ATOP.toColorFilter(
//                    ContextCompat.getColor(
//                        context,
//                        R.color.colorPrimary
//                    )
//                )
            }
            imageFoodDisplay.setImageDrawable(picture)
            Unit
        }
    }

    fun clear() {
        food = null
        with(itemView) {
            textFoodDisplayName.text = null
            textFoodDisplayCalories.text = null
            textFoodDisplayMacros.text = null
        }
    }

}

sealed class FoodDisplayItemAction(val food: Food) {
    class Select(food: Food) : FoodDisplayItemAction(food)
    class Edit(food: Food) : FoodDisplayItemAction(food)
    class Delete(food: Food) : FoodDisplayItemAction(food)
}
