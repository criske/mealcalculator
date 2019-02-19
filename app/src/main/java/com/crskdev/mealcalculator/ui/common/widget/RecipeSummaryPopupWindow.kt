package com.crskdev.mealcalculator.ui.common.widget


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.common.entities.CarbohydrateVM
import com.crskdev.mealcalculator.presentation.common.entities.FatVM
import com.crskdev.mealcalculator.presentation.common.entities.RecipeFoodVM
import com.crskdev.mealcalculator.utils.dpToPx
import com.crskdev.mealcalculator.utils.dpToPxInt
import com.crskdev.mealcalculator.utils.getColorCompat
import kotlin.math.roundToInt

/**
 * Created by Cristian Pela on 19.02.2019.
 */
class RecipeSummaryPopupWindow(val context: Context) : PopupWindow(
    null,
    ViewGroup.LayoutParams.WRAP_CONTENT,
    ViewGroup.LayoutParams.WRAP_CONTENT,
    true
) {

    private val resources = context.resources

    private val textView: TextView by lazy {
        contentView.findViewById<TextView>(android.R.id.text1)!!.apply {
            //todo obtain them window background color
            val color = Color.WHITE
            //todo refactor this into util
            background = GradientDrawable().apply {
                colors = intArrayOf(color, color, color)
                cornerRadius = 5f.dpToPx(resources)
                setStroke(
                    1f.dpToPx(resources).roundToInt(),
                    context.getColorCompat(R.color.colorPrimary)
                )
            }
            setPadding(8.dpToPxInt(resources))
        }
    }


    init {
        contentView = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(width, height)
            background = ContextCompat
                .getDrawable(context, R.drawable.round_rectangle)
        }.apply {
            LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, this, true)
        }
    }

    fun showAtLocationWith(summary: RecipeFoodVM.SummaryVM,
                           parent: View,
                           x: Int = 0,
                           y: Int = 8.dpToPxInt(resources)) {
        //bottom default
        showAsDropDownCompat(parent, x, y)
        bind(summary) {
            """
Calories: $calories
Carbs: ${carbohydrates.total()}
    Fiber: ${carbohydrates.fiber()}
    Sugar: ${carbohydrates.sugar()}
Fat: ${fat.total()}
    Saturated: ${fat.saturated()}
    Unsaturated: ${fat.unsaturated()}
Proteins: ${proteins()}
Glycemic load: ${gi()}
        """.trimIndent()
        }
    }

    fun showAtLocationWith(carbohydrate: CarbohydrateVM,
                           parent: View,
                           x: Int = 0,
                           y: Int = 8.dpToPxInt(resources)) {
        //bottom default
        showAsDropDownCompat(parent, x, y)
        bind(carbohydrate) {
            """
Total: ${total()}
Fiber: ${fiber()}
Sugar: ${sugar()}
            """.trimIndent()
        }
    }

    fun showAtLocationWith(fat: FatVM,
                           parent: View,
                           x: Int = 0,
                           y: Int = 8.dpToPxInt(resources)) {
        //bottom default
        showAsDropDownCompat(parent, x, y)
        bind(fat) {
            """
Total: ${total()}
Saturated: ${saturated()}
Unsaturated: ${unsaturated()}
            """.trimIndent()
        }
    }

    private fun showAsDropDownCompat(parent: View, x: Int, y: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            showAsDropDown(parent, x, y, Gravity.BOTTOM)
        } else {
            showAsDropDown(parent, x, parent.height - height)
        }
    }

    private inline fun <T> bind(t: T, data: T.() -> String) {
        textView.text = t.run(data)
        update()
        textView.requestFocus()
    }

    override fun dismiss() {
        textView.text = null
        super.dismiss()
    }


}