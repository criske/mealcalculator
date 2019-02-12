package com.crskdev.mealcalculator.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import com.crskdev.mealcalculator.R
import com.crskdev.mealcalculator.presentation.common.EventBusViewModel
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

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
                        findNavController().navigate(
                            HomeFragmentDirections
                                .actionHomeFragmentToFindFoodFragment(EventBusViewModel.Event.NO_CODE)
                        )
                    }
                    R.id.action_home_menu_foods -> {
                        findNavController().navigate(
                            HomeFragmentDirections
                                .actionHomeFragmentToUpsertFoodFragment(null, -1)
                        )
                    }
                    R.id.action_home_menu_meal_journal -> {
                        findNavController().navigate(
                            HomeFragmentDirections
                                .actionHomeFragmentToMealJournalFragment()
                        )
                    }
                }
                true
            }
        }
        buttonHomeNewMeal.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToMealFragment())
        }
    }

}
