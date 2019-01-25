package com.crskdev.mealcalculator.data.internal.room

import androidx.paging.DataSource
import androidx.room.*
import com.crskdev.mealcalculator.data.internal.room.entities.FoodDb
import com.crskdev.mealcalculator.data.internal.room.entities.Tables

/**
 * Created by Cristian Pela on 25.01.2019.
 */
@Dao
internal interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(foodDb: FoodDb)

    @Delete
    fun delete(foodDb: FoodDb)

    @Query("SELECT * FROM ${Tables.FOODS} WHERE name LIKE :like ORDER BY name")
    fun find(like: String): DataSource.Factory<Int, FoodDb>

    @Query("SELECT * FROM ${Tables.FOODS} ORDER BY name")
    fun findAll(): DataSource.Factory<Int, FoodDb>

}