package com.crskdev.mealcalculator.platform

import androidx.room.InvalidationTracker
import com.crskdev.mealcalculator.data.internal.room.MealCalculatorDatabase
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.gateway.FoodRepository
import com.crskdev.mealcalculator.domain.gateway.RecipeFoodEntriesManager

/**
 * Created by Cristian Pela on 31.01.2019.
 */
class PlatformRecipeFoodEntriesManager(
    private val db: MealCalculatorDatabase,
    foodRepository: FoodRepository,
    private val delegate: RecipeFoodEntriesManager) : RecipeFoodEntriesManager by delegate {

    private val dbTrackObserver = object : InvalidationTracker.Observer("foods") {
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
    }

    fun unTrackDbChanges() {
        db.invalidationTracker.removeObserver(dbTrackObserver)
    }


}