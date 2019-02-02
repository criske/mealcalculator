package com.crskdev.mealcalculator.data.internal.room

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crskdev.mealcalculator.data.internal.room.entities.MealDb
import com.crskdev.mealcalculator.data.internal.room.entities.Tables

/**
 * Created by Cristian Pela on 25.01.2019.
 */
@Dao
internal interface MealDao {

    @Query("SELECT * FROM ${Tables.MEALS_JOURNAL} WHERE date=date('now') LIMIT 1")
    fun getAllTodayMeal(): MealDb?

    @Query("SELECT * FROM ${Tables.MEALS_JOURNAL} WHERE m_id=:id")
    fun getJournalMealById(id: Long): MealDb

    @Query("SELECT * FROM ${Tables.MEALS_JOURNAL} ORDER BY date DESC")
    fun getAllJournalMeals(): DataSource.Factory<Int, MealDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mealDb: MealDb)

    @Query("SELECT numberOfTheDay FROM ${Tables.MEALS_JOURNAL} WHERE date=date('now')")
    fun getAllTodayMealCount(): Int

    @Query("SELECT m_id FROM ${Tables.MEALS_JOURNAL} WHERE date=date('now')")
    fun getAllTodayMealId(): Long

    @Query("DELETE FROM ${Tables.MEALS_JOURNAL} WHERE m_id=:id")
    fun deleteMealFromJournal(id: Long)

}