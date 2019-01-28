package com.crskdev.mealcalculator.domain.gateway

import com.crskdev.mealcalculator.domain.entities.Food

interface FoodRepository : Transactionable<FoodRepository> {

    fun create(vararg food: Food): LongArray

    fun createSingle(food: Food): Long

    fun edit(food: Food)

    fun delete(food: Food)

    fun findById(id: Long): Food?

    suspend fun find(like: String, observer: (List<Food>) -> Unit)

    suspend fun findAll(observer: (List<Food>) -> Unit)

}

interface Transactionable<T> {
    fun runTransaction(block: T.() -> Unit)
}