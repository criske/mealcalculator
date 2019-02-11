package com.crskdev.mealcalculator.data.internal.room.entities

/**
 * Created by Cristian Pela on 25.01.2019.
 */
internal object Tables : Iterable<String> {
    //main tables
    const val FOODS = "foods"
    const val MEALS_JOURNAL = "meals_journal"
    const val RECIPES = "recipes"

    //relation tables
    const val CURRENT_MEAL_ENTRIES = "current_meal_entries"
    const val RECIPES_FOODS = "recipes_foods"

    override fun iterator(): Iterator<String> = object : Iterator<String> {

        val total = 5

        var index = 0

        override fun hasNext(): Boolean = index + 1 <= total

        override fun next(): String = when (index++) {
            0 -> FOODS
            1 -> RECIPES
            2 -> MEALS_JOURNAL
            3 -> RECIPES_FOODS
            4 -> CURRENT_MEAL_ENTRIES
            else -> throw IndexOutOfBoundsException("No table at index $index")
        }

    }
}