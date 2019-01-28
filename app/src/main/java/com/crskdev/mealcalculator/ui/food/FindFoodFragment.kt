package com.crskdev.mealcalculator.ui.food


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.dependencyGraph
import com.crskdev.mealcalculator.presentation.food.FindFoodViewModel
import com.crskdev.mealcalculator.utils.viewModelFromProvider
import kotlinx.android.synthetic.main.fragment_find_food.*

/**
 * A simple [Fragment] subclass.
 *
 */
class FindFoodFragment : Fragment() {

    lateinit var viewModel: FindFoodViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModelFromProvider(this) {
            with(context!!.dependencyGraph()) {
                FindFoodViewModel(findFoodInteractor())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_food, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editFoodsSearch.doAfterTextChanged { e ->
            e?.also {
                viewModel.search(it.toString())
            }
        }
        viewModel.foodsLiveData.observe(this, Observer {
            textFoods.text = it.joinToString("\n")
        })

    }


}
