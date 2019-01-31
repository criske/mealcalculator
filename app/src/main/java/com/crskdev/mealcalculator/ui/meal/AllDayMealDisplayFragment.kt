package com.crskdev.mealcalculator.ui.meal


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import kotlinx.android.synthetic.main.fragment_all_day_meal_display.*

/**
 * A simple [Fragment] subclass.
 *
 */
class AllDayMealDisplayFragment : DiFragment() {

    private val viewModel by lazy {
        di.allDayMealDisplayViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_day_meal_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.allDayMealLiveData.observe(this, Observer {
            val display = """
                All Day Meals Macros:

                Calories: ${it.calories}
                Carbs: ${it.carbohydrate.total}
                    Fiber: ${it.carbohydrate.fiber}
                    Sugar: ${it.carbohydrate.sugar}
                Fat: ${it.fat.total}
                    Saturated: ${it.fat.saturated}
                    Unsaturated: ${it.fat.unsaturated}
                Proteins: ${it.protein}
                Glycemic load: ${it.glycemicLoad}
            """.trimIndent()
            textAllDayMealDisplay.text = display
        })
    }

}
