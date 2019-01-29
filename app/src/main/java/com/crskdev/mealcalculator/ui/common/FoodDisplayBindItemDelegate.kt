package com.crskdev.mealcalculator.ui.common

import android.view.MenuInflater
import android.view.View
import androidx.core.view.forEach
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.presentation.common.SelectedFoodViewModel
import com.crskdev.mealcalculator.utils.ProjectDrawableUtils
import com.crskdev.mealcalculator.utils.simpleAlertDialog
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
                    //action(FoodDisplayItemAction.Select(it))
                    action(FoodDisplayItemAction.Edit(it))
                }
            }
//            setOnCreateContextMenuListener { menu, view, info ->
//                with(menu) {
//                    MenuInflater(context).inflate(R.menu.menu_conxtextual_food_display_item, this)
//                    forEach { item ->
//                        when (item.itemId) {
//                            R.id.menu_action_food_edit -> {
//                                food?.also { action(FoodDisplayItemAction.Edit(it)) }
//                            }
//                            R.id.menu_action_food_delete -> {
//                                food?.also {
//                                    context.simpleAlertDialog(
//                                        context.getString(R.string.warning),
//                                        context.getString(R.string.msg_warning_delete_food, it.name)
//                                    ) {
//                                        action(FoodDisplayItemAction.Edit(it))
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//            }
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
            food.picture?.also {
                imageFoodDisplay.setImageDrawable(
                    ProjectDrawableUtils.convertStrToRoundedDrawable(resources, it)
                )
            } ?: imageFoodDisplay.setImageResource(R.drawable.ic_food_black_64dp)
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