package com.crskdev.mealcalculator.data.internal.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.crskdev.mealcalculator.data.internal.room.entities.MealEntryDb
import com.crskdev.mealcalculator.data.internal.room.entities.MealEntryWithFoodDb
import com.crskdev.mealcalculator.data.internal.room.entities.Tables

/**
 * Created by Cristian Pela on 25.01.2019.
 */
@Dao
internal interface MealEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mealEntry: MealEntryDb)

    @Update
    fun update(mealEntry: MealEntryDb)

    @Delete
    fun delete(mealEntry: MealEntryDb)

    @Query("DELETE FROM ${Tables.CURRENT_MEAL_ENTRIES}")
    fun clear()

    @Query(
        """
        SELECT me.*,f.* FROM ${Tables.CURRENT_MEAL_ENTRIES} me, ${Tables.FOODS} f
            WHERE me.fk_food_id == food_id ORDER BY me.fk_food_id
    """
    )
    fun observeEntries(): LiveData<List<MealEntryWithFoodDb>>

    @Query(
        """
        SELECT me.*,f.* FROM ${Tables.CURRENT_MEAL_ENTRIES} me, ${Tables.FOODS} f
            WHERE me.fk_food_id == food_id AND fk_food_id =:foodId ORDER BY me.fk_food_id LIMIT 1
    """
    )
    fun getMealEntryForFoodId(foodId: Long): MealEntryWithFoodDb?

}