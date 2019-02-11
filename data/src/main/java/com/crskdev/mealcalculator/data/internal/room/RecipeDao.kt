package com.crskdev.mealcalculator.data.internal.room

import androidx.room.Dao
import androidx.room.Query
import com.crskdev.mealcalculator.data.internal.room.entities.RecipeDetailedDb
import com.crskdev.mealcalculator.data.internal.room.entities.Tables

/**
 * Created by Cristian Pela on 11.02.2019.
 */
@Dao
internal interface RecipeDao {

    @Query(
        """
        SELECT r.*, rf.rf_quantity, f.*
         FROM ${Tables.RECIPES} r, ${Tables.RECIPES_FOODS} rf
         LEFT JOIN ${Tables.FOODS} f ON rf.rf_fk_f_id == f.food_id
         WHERE rf.rf_fk_r_id == r.r_id AND r.r_id =:id
    """
    )
    fun getRecipeById(id: Long): RecipeDetailedDb?

}