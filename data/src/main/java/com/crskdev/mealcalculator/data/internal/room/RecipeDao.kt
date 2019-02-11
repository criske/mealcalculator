package com.crskdev.mealcalculator.data.internal.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.crskdev.mealcalculator.data.internal.room.entities.RecipeDb
import com.crskdev.mealcalculator.data.internal.room.entities.RecipeDetailedDb
import com.crskdev.mealcalculator.data.internal.room.entities.RecipeFoodDb

/**
 * Created by Cristian Pela on 11.02.2019.
 */
@Dao
internal interface RecipeDao {

    @Query(
        """
        SELECT rf.rf_id, r.*, rf.rf_quantity, f.*
         FROM recipes r, recipes_foods rf
         LEFT JOIN foods f ON rf.rf_fk_f_id == f.food_id
         WHERE rf.rf_fk_r_id == r.r_id AND r.r_id =:id
    """
    )
    fun getRecipeDetailedById(id: Long): List<RecipeDetailedDb>

    @Query(
        """
        SELECT  rf.rf_id, r.*, rf.rf_quantity, f.*
         FROM recipes r, recipes_foods rf
         LEFT JOIN foods f ON rf.rf_fk_f_id == f.food_id
         WHERE rf.rf_fk_r_id == r.r_id AND r.r_id =:id
    """
    )
    fun observeRecipeDetailedById(id: Long): LiveData<List<RecipeDetailedDb>>

    @Query("SELECT * FROM recipes WHERE r_id=:id")
    fun getRecipeById(id: Long): RecipeDb

    @Insert
    fun insertRecipe(recipe: RecipeDb): Long

    @Update
    fun updateRecipe(recipe: RecipeDb)

    @Insert
    fun insertRecipeFood(recipeFood: RecipeFoodDb): Long

    @Insert
    fun updateRecipeFood(recipeFood: RecipeFoodDb)

    @Delete
    fun deleteRecipeFood(recipeFood: RecipeFoodDb)

    @Delete
    fun deleteRecipe(recipe: RecipeDb)

    @Query("SELECT * FROM recipes")
    fun observeAll(): LiveData<List<RecipeDb>>

}