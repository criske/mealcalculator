package com.crskdev.mealcalculator.platform

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.room.InvalidationTracker
import com.crskdev.mealcalculator.MainActivity
import com.crskdev.mealcalculator.data.internal.room.MealCalculatorDatabase
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.gateway.FoodRepository
import com.crskdev.mealcalculator.domain.gateway.RecipeFoodEntriesManager
import com.crskdev.mealcalculator.ui.meal.MealFragment

/**
 * Created by Cristian Pela on 31.01.2019.
 */
class PlatformRecipeFoodEntriesManager(
    activity: MainActivity,
    db: MealCalculatorDatabase,
    foodRepository: FoodRepository,
    private val delegate: RecipeFoodEntriesManager) : RecipeFoodEntriesManager by delegate {

    val dbTrackObserver = object : InvalidationTracker.Observer("foods") {
        override fun onInvalidated(tables: MutableSet<String>) {
            val entries = this@PlatformRecipeFoodEntriesManager.getAll()
            val entriesFoodChanged = foodRepository.findAllByIds(entries.map { it.food.id })

            val entriesChanged = mutableListOf<RecipeFood>()
            entriesFoodChanged.forEach { f ->
                entries.firstOrNull { it.food.id == f.id }
                    ?.also {
                        entriesChanged.add(it.copy(food = f))
                    }
            }

            notifyEntriesChanged(entriesChanged)
        }
    }

    init {
        db.invalidationTracker.addObserver(dbTrackObserver)
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                if (f::class == MealFragment::class) {
                    db.invalidationTracker.removeObserver(dbTrackObserver)
                }
            }
        }, true)
    }


}