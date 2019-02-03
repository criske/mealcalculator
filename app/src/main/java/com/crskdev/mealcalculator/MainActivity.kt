package com.crskdev.mealcalculator

import android.os.Bundle
import androidx.navigation.findNavController
import com.crskdev.mealcalculator.ui.common.HasBackPressedAwareness
import com.crskdev.mealcalculator.ui.common.di.DiActivity
import com.crskdev.mealcalculator.utils.navHostFragmentChildFragmentManager
import com.crskdev.mealcalculator.utils.onBackPressedToExit

class MainActivity : DiActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dependencyGraph().inject(this)
        setContentView(R.layout.activity_main)
    }

    override fun onNavigateUp(): Boolean = findNavController(R.id.navContainer).navigateUp()


    override fun onBackPressed() {
        if (!onBackPressedToExit(isUsingNavHostFragment = true)) {
            navHostFragmentChildFragmentManager.fragments
                .firstOrNull()
                ?.apply {
                    //todo handle child fragments with HasBackPressedAwareness?
                    if (this is HasBackPressedAwareness) {
                        if (!handleBackPressed())
                            super.onBackPressed()
                    } else {
                        super.onBackPressed()
                    }
                }

        }
    }
}
