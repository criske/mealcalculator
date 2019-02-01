package com.crskdev.mealcalculator.data

import com.crskdev.mealcalculator.data.internal.room.MealCalculatorDatabase
import com.crskdev.mealcalculator.data.internal.room.entities.toDb
import com.crskdev.mealcalculator.data.internal.room.entities.toDomain
import com.crskdev.mealcalculator.data.internal.runTransactionDelegate
import com.crskdev.mealcalculator.data.internal.utils.onPaging
import com.crskdev.mealcalculator.data.internal.utils.setupPagedListBuilder
import com.crskdev.mealcalculator.data.internal.utils.toChannel
import com.crskdev.mealcalculator.domain.entities.Meal
import com.crskdev.mealcalculator.domain.entities.MealEntry
import com.crskdev.mealcalculator.domain.gateway.MealRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope

/**
 * Created by Cristian Pela on 25.01.2019.
 */
@ExperimentalCoroutinesApi
class MealRepositoryImpl(private val db: MealCalculatorDatabase) : MealRepository {

    private val mealDao by lazy {
        db.mealDao()
    }

    private val mealEntryDao by lazy {
        db.mealEntryDao()
    }

    override fun getAllTodayMeal(): Meal? =
        mealDao.getAllTodayMeal()?.toDomain()

    override fun getJournalMealById(id: Long): Meal =
        mealDao.getJournalMealById(id).toDomain()

    override suspend fun observeJournalMeals(observer: (List<Meal>) -> Unit) =
        coroutineScope {
            mealDao.getAllJournalMeals()
                .mapByPage { p -> p.map { it.toDomain() } }
                .setupPagedListBuilder(10)
                .onPaging { pagedList, _ ->
                    observer(pagedList)
                }
        }


    override fun saveAllToday(meal: Meal) {
        mealDao.insert(meal.toDb())
    }

    override fun getAllTodayMealCount(): Int = mealDao.getAllTodayMealCount()

    override fun getAllTodayMealId(): Long = mealDao.getAllTodayMealId()

    override fun startAllTodayMeal(meal: Meal) =
        mealDao.insert(meal.toDb())

    override fun existentCurrentMealEntryWithFood(foodId: Long): MealEntry? =
        mealEntryDao.getMealEntryForFoodId(foodId)?.toDomain()

    override fun addCurrentMealEntry(mealEntry: MealEntry) {
        mealEntryDao.insert(mealEntry.toDb())
    }

    override fun editCurrentMealEntry(mealEntry: MealEntry) {
        mealEntryDao.update(mealEntry.toDb())
    }

    override fun removeCurrentMealEntry(mealEntry: MealEntry) {
        mealEntryDao.delete(mealEntry.toDb())
    }

    override fun discardCurrentMealEntries() {
        mealEntryDao.clear()
    }

    override suspend fun observeCurrentMealEntries(observer: (List<MealEntry>) -> Unit) =
        coroutineScope {
            mealEntryDao.observeEntries().toChannel { ch ->
                for (list in ch) {
                    observer(list.map { it.toDomain() })
                }
            }
        }

    override fun runTransaction(block: MealRepository.() -> Unit) =
        runTransactionDelegate(db, block)


}