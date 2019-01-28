package com.crskdev.mealcalculator

import android.os.Bundle
import androidx.navigation.findNavController
import com.crskdev.mealcalculator.ui.common.di.DiActivity

class MainActivity : DiActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dependencyGraph().inject(this)
        setContentView(R.layout.activity_main)
    }

    override fun onNavigateUp(): Boolean = findNavController(R.id.navContainer).navigateUp()

}
