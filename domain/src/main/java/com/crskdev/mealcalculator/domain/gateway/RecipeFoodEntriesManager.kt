package com.crskdev.mealcalculator.domain.gateway

import com.crskdev.mealcalculator.domain.entities.RecipeFood

/**
 * Created by Cristian Pela on 31.01.2019.
 */
interface RecipeFoodEntriesManager {

    operator fun plus(entry: RecipeFood): Boolean

    operator fun minus(entry: RecipeFood): Boolean

    fun add(entry: RecipeFood, autoNotify: Boolean = true)

    fun addAll(entries: List<RecipeFood>, autoNotify: Boolean = true)

    fun contains(recipeFood: RecipeFood): Boolean

    fun getAll(): List<RecipeFood>

    fun observeAll(observer: (List<RecipeFood>) -> Unit)

    fun getEntryWithFoodId(id: Long): RecipeFood?

    fun update(entry: RecipeFood)

    fun notifyEntriesChanged(list: List<RecipeFood>)

    fun notifyObservers()
}


class RecipeFoodEntriesManagerImpl : RecipeFoodEntriesManager {

    private val entries = mutableListOf<RecipeFood>()

    private val observers = mutableListOf<(List<RecipeFood>) -> Unit>()

    @Synchronized
    override fun addAll(entries: List<RecipeFood>, autoNotify: Boolean) {
        this.entries.addAll(0, entries)
        if (autoNotify)
            notifyObservers()
    }

    @Synchronized
    override fun add(entry: RecipeFood, autoNotify: Boolean) {
        this.entries.add(0, entry)
        if (autoNotify)
            notifyObservers()
    }

    @Synchronized
    override fun plus(entry: RecipeFood): Boolean {
        entries.add(0, entry)

        notifyObservers()
        return true
    }

    @Synchronized
    override fun minus(entry: RecipeFood): Boolean =
        entries.remove(entry).apply {
            if (this) {
                notifyObservers()
            }
        }

    @Synchronized
    override fun contains(recipeFood: RecipeFood): Boolean {
        return entries.any { it.food.id == recipeFood.food.id }
    }

    @Synchronized
    override fun getAll(): List<RecipeFood> =
        entries.toList()

    @Synchronized
    override fun update(entry: RecipeFood) {
        val position = entries.indexOfFirst {
            it.food == entry.food
        }
        if (position != -1) {
            entries[position] = entry
            notifyObservers()
        }

    }

    @Synchronized
    override fun getEntryWithFoodId(id: Long): RecipeFood? =
        entries.firstOrNull {
            it.food.id == id
        }

    @Synchronized
    override fun observeAll(observer: (List<RecipeFood>) -> Unit) {
        observers.add(observer)
        if (entries.isNotEmpty())
            observer(getAll())
    }

    @Synchronized
    override fun notifyEntriesChanged(list: List<RecipeFood>) {
        entries.clear()
        entries.addAll(list)
        notifyObservers()
    }

    @Synchronized
    override fun notifyObservers() {
        if (entries.isNotEmpty()) {
            val all = getAll()
            observers.forEach { it.invoke(all) }
        }
    }

}