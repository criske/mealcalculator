@file:Suppress("unused")

package com.crskdev.mealcalculator.ui.common.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.core.view.forEach
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.utils.dpToPx
import com.crskdev.mealcalculator.utils.getColorCompat
import kotlinx.android.synthetic.main.recipe_summary_view_layout.view.*
import kotlin.math.roundToInt

/**
 * Created by Cristian Pela on 18.02.2019.
 */
class RecipeSummaryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.recipe_summary_view_layout, this, true)
            .cast<ViewGroup>()
            .getChildAt(0)
            .cast<ViewGroup>().forEach { v ->
                v.takeIf { it is TextView }
                    ?.background
                    ?.takeIf { it is ColorDrawable }
                    ?.cast<ColorDrawable>()
                    ?.color
                    ?.also { bg ->
                        v.background = GradientDrawable().apply {
                            colors = intArrayOf(bg, bg, bg)
                            cornerRadius = 5f.dpToPx(resources)
                            setStroke(
                                1f.dpToPx(resources).roundToInt(),
                                context.getColorCompat(R.color.colorPrimary)
                            )
                        }
                        if (ColorUtils.calculateLuminance(bg) < 0.5)
                            v.cast<TextView>().setTextColor(
                                context.getColorCompat(android.R.color.white)
                            )
                    }
            }
    }


    fun bind(calories: Int, carbohydrates: Float, fats: Float, proteins: Float, gl: Float) {
        textRecipeSummaryViewCalories.text = "kCal.\n$calories"
        textRecipeSummaryViewCarbs.text = "C\n" + carbohydrates.format()
        textRecipeSummaryViewFats.text = "F\n" + fats.format()
        textRecipeSummaryViewProteins.text = "P\n" + proteins.format()
        textRecipeSummaryViewGL.text = "GL\n" + gl.format()
    }

    private fun Float.format(): String = "%.2f".format(this)

}