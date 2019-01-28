package com.crskdev.mealcalculator.data.internal.room

import androidx.paging.DataSource
import androidx.room.*
import com.crskdev.mealcalculator.data.internal.room.entities.FoodDb
import com.crskdev.mealcalculator.data.internal.room.entities.Tables
import com.crskdev.mealcalculator.domain.entities.Food

/**
 * Created by Cristian Pela on 25.01.2019.
 */
@Dao
internal interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg foodDb: FoodDb): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingle(foodDb: FoodDb): Long

    @Delete
    fun delete(foodDb: FoodDb)

    @Query("SELECT * FROM ${Tables.FOODS} WHERE name LIKE :like ORDER BY name ASC")
    fun find(like: String): DataSource.Factory<Int, FoodDb>

    @Query("SELECT * FROM ${Tables.FOODS} ORDER BY name ASC")
    fun findAll(): DataSource.Factory<Int, FoodDb>

    @Query("SELECT * FROM ${Tables.FOODS} WHERE food_id=:id")
    fun findById(id: Long): FoodDb?

}