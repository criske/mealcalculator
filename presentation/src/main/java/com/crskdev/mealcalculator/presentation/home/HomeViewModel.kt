package com.crskdev.mealcalculator.presentation.home

import androidx.lifecycle.ViewModel

/**
 * Created by Cristian Pela on 04.03.2019.
 */
class HomeViewModel(private val router: HomeViewModelRouter) : ViewModel(),
    HomeViewModelRouter by router