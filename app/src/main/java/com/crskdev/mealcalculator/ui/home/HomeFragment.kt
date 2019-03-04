package com.crskdev.mealcalculator.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.home.HomeViewModel
import com.crskdev.mealcalculator.ui.common.di.DiFragment
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : DiFragment() {

    private val viewModel: HomeViewModel by lazy {
        di.homeViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(toolbarHome) {
            inflateMenu(R.menu.menu_home)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_home_menu_foods -> {
                        viewModel.routeToFoods()
                    }
                    R.id.action_home_menu_meal_journal -> {
                        viewModel.routeToMealJournal()
                    }
                    R.id.action_home_menu_recipes -> {
                        viewModel.routeToRecipes()
                    }
                }
                true
            }
        }
        buttonHomeNewMeal.setOnClickListener {
            viewModel.routeToNewMeal()
        }
    }

}
