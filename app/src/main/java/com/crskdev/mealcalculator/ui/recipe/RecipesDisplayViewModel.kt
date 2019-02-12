package com.crskdev.mealcalculator.ui.recipe

import com.crskdev.mealcalculator.domain.interactors.RecipesGetInteractor
import com.crskdev.mealcalculator.presentation.common.CoroutineScopedViewModel

/**
 * Created by Cristian Pela on 12.02.2019.
 */
class RecipesDisplayViewModel(
    private val recipesGetInteractor: RecipesGetInteractor
) : CoroutineScopedViewModel()