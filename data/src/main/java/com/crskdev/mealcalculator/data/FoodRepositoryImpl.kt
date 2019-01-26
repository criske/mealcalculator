package com.crskdev.mealcalculator.data

import com.crskdev.arch.coroutines.paging.onPaging
import com.crskdev.arch.coroutines.paging.setupPagedListBuilder
import com.crskdev.mealcalculator.data.internal.runTransactionDelegate
import com.crskdev.mealcalculator.data.internal.room.MealCalculatorDatabase
import com.crskdev.mealcalculator.data.internal.room.entities.toDb
import com.crskdev.mealcalculator.data.internal.room.entities.toDomain
import com.crskdev.mealcalculator.domain.entities.Food
import com.crskdev.mealcalculator.domain.gateway.FoodRepository
import kotlinx.coroutines.coroutineScope

/**
 * Created by Cristian Pela on 25.01.2019.
 */
class FoodRepositoryImpl(private val db: MealCalculatorDatabase) : FoodRepository {

    private val foodDao by lazy {
        db.foodDao()
    }

    override fun create(food: Food): Long =
        foodDao.insert(food.toDb())

    override fun edit(food: Food) {
        foodDao.insert(food.toDb())
    }

    override fun delete(food: Food) {
        foodDao.delete(food.toDb())
    }

    override fun findById(id: Long): Food? =
        foodDao.findById(id)?.toDomain()

    override suspend fun find(like: String, observer: (List<Food>) -> Unit) =
        coroutineScope {
            foodDao.find("%$like%")
                .mapByPage { p -> p.map { it.toDomain() } }
                .setupPagedListBuilder(10)
                .onPaging { pagedList, _ ->
                    observer(pagedList)
                }
        }

    override suspend fun findAll(observer: (List<Food>) -> Unit) =
        coroutineScope {
            foodDao.findAll()
                .mapByPage { p -> p.map { it.toDomain() } }
                .setupPagedListBuilder(10)
                .onPaging { pagedList, _ ->
                    observer(pagedList)
                }
        }

    override fun runTransaction(block: FoodRepository.() -> Unit) =
        runTransactionDelegate(db, block)
}