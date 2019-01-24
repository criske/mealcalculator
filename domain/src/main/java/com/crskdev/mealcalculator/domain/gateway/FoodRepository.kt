package com.crskdev.mealcalculator.domain.gateway

import com.crskdev.mealcalculator.domain.entities.Food

interface FoodRepository : Transactionable<FoodRepository> {

    fun create(food: Food)

    fun edit(food: Food)

    fun delete(id: Int)

    suspend fun search(like: String): List<Food>

}

interface Transactionable<T> {
    fun runTransaction(block: T.() -> Unit)
}