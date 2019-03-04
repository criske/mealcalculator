@file:Suppress("unused")

package com.crskdev.mealcalculator.ui.common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.ColorUtils
import androidx.core.view.forEach
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.common.entities.RecipeFoodVM
import com.crskdev.mealcalculator.presentation.common.utils.cast
import com.crskdev.mealcalculator.utils.dpToPx
import com.crskdev.mealcalculator.utils.getColorCompat
import kotlinx.android.synthetic.main.recipe_summary_view_layout.view.*

/**
 * Created by Cristian Pela on 18.02.2019.
 */
class RecipeSummaryView : HorizontalScrollView {

    private val summaryPopupWindow by lazy {
        RecipeSummaryPopupWindow(context)
    }

    private var summary: RecipeFoodVM.SummaryVM? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {

        var isDecorated = true
        context.withStyledAttributes(attrs, R.styleable.RecipeSummaryView) {
            isDecorated = getBoolean(R.styleable.RecipeSummaryView_decorated, true)
        }


        LayoutInflater
            .from(context)
            .inflate(R.layout.recipe_summary_view_layout, this, true)
            .cast<ViewGroup>()
            .getChildAt(0)
            .cast<ViewGroup>().forEach { v ->
                v.takeIf { it is TextView }
                    ?.cast<TextView>()
                    ?.also { v ->
                        //todo refactor this into util
                        val bgColor = if (v.background is ColorDrawable)
                            v.background.cast<ColorDrawable>().color
                        else
                            Color.WHITE

                        v.background = if (isDecorated) {
                            GradientDrawable().apply {
                                colors = intArrayOf(bgColor, bgColor, bgColor)
                                cornerRadius = 5f.dpToPx(resources)
                                setStroke(
                                    resources.getDimensionPixelSize(R.dimen.default_border_thick),
                                    context.getColorCompat(R.color.colorPrimary)
                                )
                            }
                        } else {
                            null
                        }


                        if (isDecorated) {
                            if (ColorUtils.calculateLuminance(bgColor) < 0.5)
                                v.setTextColor(Color.WHITE)
                        } else {
                            v.setTextColor(Color.DKGRAY)
                        }

//                        v.layoutParams = v.layoutParams.apply {
//                            width = 36.dpToPx(resources).roundToInt()
//                            height = 36.dpToPx(resources).roundToInt()
//                        }
                    }
            }
        textRecipeSummaryViewCalories.setOnClickListener { v ->
            summary?.also {
                summaryPopupWindow.showAtLocationWith(it, v)
            }
        }
        textRecipeSummaryViewCarbs.setOnClickListener { v ->
            summary?.also {
                summaryPopupWindow.showAtLocationWith(it.carbohydrates, v)
            }
        }
        textRecipeSummaryViewFats.setOnClickListener { v ->
            summary?.also {
                summaryPopupWindow.showAtLocationWith(it.fat, v)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    fun bind(summary: RecipeFoodVM.SummaryVM) {
        this.summary = summary
        textRecipeSummaryViewCalories.text = "kCal.\n${summary.calories}"
        textRecipeSummaryViewCarbs.text = "C\n" + summary.carbohydrates.total()
        textRecipeSummaryViewFats.text = "F\n" + summary.fat.total()
        textRecipeSummaryViewProteins.text = "P\n" + summary.proteins()
        textRecipeSummaryViewGL.text = "GL\n" + summary.gi()
    }

}