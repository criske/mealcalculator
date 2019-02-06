package com.crskdev.mealcalculator.domain.gateway

import com.crskdev.mealcalculator.domain.entities.RecipeFood
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

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

    private val lock = ReentrantReadWriteLock()

    override fun addAll(entries: List<RecipeFood>, autoNotify: Boolean) {
        lock.writeLock().withLock {
            this.entries.addAll(0, entries)
            if (autoNotify)
                notifyObservers()
        }
    }

    override fun add(entry: RecipeFood, autoNotify: Boolean) {
        lock.writeLock().withLock {
            this.entries.add(0, entry)
            if (autoNotify)
                notifyObservers()
        }
    }


    override fun plus(entry: RecipeFood): Boolean =
        lock.writeLock().withLock {
            entries.add(0, entry)
            notifyObservers()
            true
        }


    override fun minus(entry: RecipeFood): Boolean =
        lock.writeLock().withLock {
            entries.remove(entry).apply {
                if (this) {
                    notifyObservers()
                }
            }
        }

    override fun contains(recipeFood: RecipeFood): Boolean {
        return lock.readLock().withLock {
            entries.any { it.food.id == recipeFood.food.id }
        }
    }


    override fun getAll(): List<RecipeFood> = lock.writeLock().withLock {
        entries.toList()
    }


    override fun update(entry: RecipeFood) {
        lock.writeLock().withLock {
            val position = entries.indexOfFirst {
                it.food == entry.food
            }
            if (position != -1) {
                entries[position] = entry
                notifyObservers()
            }
        }
    }

    override fun getEntryWithFoodId(id: Long): RecipeFood? =
        lock.readLock().withLock {
            entries.firstOrNull {
                it.food.id == id
            }
        }

    override fun observeAll(observer: (List<RecipeFood>) -> Unit) {
        lock.writeLock().withLock {
            observers.add(observer)
        }
    }

    override fun notifyEntriesChanged(list: List<RecipeFood>) {
        lock.writeLock().withLock {
            entries.clear()
            entries.addAll(list)
            notifyObservers()
        }
    }

    override fun notifyObservers() {
        lock.readLock().withLock {
            observers.forEach { it.invoke(getAll()) }
        }
    }

}