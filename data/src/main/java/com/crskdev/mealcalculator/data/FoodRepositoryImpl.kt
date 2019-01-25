package com.crskdev.mealcalculator.data

import com.crskdev.mealcalculator.data.internal.runTransactionDelegate
import com.crskdev.mealcalculator.data.internal.room.MealCalculatorDatabase
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.gateway.FoodRepository

/**
 * Created by Cristian Pela on 25.01.2019.
 */
class FoodRepositoryImpl(private val db: MealCalculatorDatabase) : FoodRepository {

    override fun create(food: Food) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun edit(food: Food) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun search(like: String): List<Food> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun runTransaction(block: FoodRepository.() -> Unit) =
        runTransactionDelegate(db, block)
}