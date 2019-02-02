package com.crskdev.mealcalculator.domain.gateway

import com.crskdev.mealcalculator.domain.entities.MealEntry
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

/**
 * Created by Cristian Pela on 31.01.2019.
 */
interface CurrentMealEntryManager {

    operator fun plus(mealEntry: MealEntry): Boolean

    operator fun minus(mealEntry: MealEntry): Boolean

    fun getAll(): List<MealEntry>

    fun observeAll(observer: (List<MealEntry>) -> Unit)

    fun getEntryWithFoodId(id: Long): MealEntry?

    fun update(mealEntry: MealEntry)

    fun notifyEntriesChanged(list: List<MealEntry>)
}


class CurrentMealEntryManagerImpl : CurrentMealEntryManager {

    private val idGenerator = AtomicLong()

    private val entries = mutableListOf<MealEntry>()

    private val observers = mutableListOf<(List<MealEntry>) -> Unit>()

    private val lock = ReentrantReadWriteLock()

    override fun plus(mealEntry: MealEntry): Boolean =
        lock.writeLock().withLock {
            entries.add(0, mealEntry.copy(id = idGenerator.incrementAndGet()))
            notifyObservers()
            true
        }


    override fun minus(mealEntry: MealEntry): Boolean =
        lock.writeLock().withLock {
            entries.remove(mealEntry).apply {
                if (this) {
                    notifyObservers()
                }
            }
        }

    override fun getAll(): List<MealEntry> = lock.writeLock().withLock {
        entries.toList()
    }


    override fun update(mealEntry: MealEntry) {
        lock.writeLock().withLock {
            val position = entries.indexOfFirst {
                it.id == mealEntry.id
            }
            if (position != -1) {
                entries[position] = mealEntry
                notifyObservers()
            }
        }
    }

    override fun getEntryWithFoodId(id: Long): MealEntry? =
        lock.readLock().withLock {
            entries.firstOrNull {
                it.food.id == id
            }
        }

    override fun observeAll(observer: (List<MealEntry>) -> Unit) {
        lock.writeLock().withLock {
            observers.add(observer)
        }
    }

    override fun notifyEntriesChanged(list: List<MealEntry>) {
        lock.writeLock().withLock {
            entries.clear()
            entries.addAll(list)
            notifyObservers()
        }
    }

    private fun notifyObservers() {
        lock.readLock().withLock {
            observers.forEach { it.invoke(getAll()) }
        }
    }

}