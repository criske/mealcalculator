package com.crskdev.mealcalculator.ui.recipe


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.ui.common.di.DiFragment

class RecipesDisplayFragment : DiFragment() {

    private val viewModel by lazy {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes_display, container, false)
    }
}
