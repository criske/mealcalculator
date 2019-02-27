package com.crskdev.mealcalculator.ui.meal


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import kotlinx.android.synthetic.main.fragment_meal_journal_detail.*

class MealJournalDetailFragment : DiFragment() {

    private val viewModel by lazy {
        di.mealJournalDetailViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal_journal_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.allDayMealLiveData.observe(this, Observer {
            val display = """
                Date: ${it.date}
                Meals: ${it.numberOfTheDay}

                Calories: ${it.summary.calories}
                Carbs: ${it.summary.carbohydrates.total}
                    Fiber: ${it.summary.carbohydrates.fiber}
                    Sugar: ${it.summary.carbohydrates.sugar}
                Fat: ${it.summary.fat.total}
                    Saturated: ${it.summary.fat.saturated}
                    Unsaturated: ${it.summary.fat.unsaturated}
                Proteins: ${it.summary.proteins}
                Glycemic load: ${it.summary.gi}
            """.trimIndent()
            textAllDayMealDisplay.text = display
        })
    }

}
