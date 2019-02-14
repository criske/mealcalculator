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
internal abstract class RecipeDao {

    companion object {
        private const val GET_RECIPE_DETAILED_BY_ID = """
                 SELECT r.*, rf.* FROM recipes r LEFT JOIN (
                     SELECT  rf.*, f.*
                            FROM foods f, recipes_foods rf
                                WHERE f.food_id == rf_fk_f_id) rf
                        ON rf.rf_fk_r_id == r.r_id
                 WHERE r.r_id =:id
        """
    }

    @Query(GET_RECIPE_DETAILED_BY_ID)
    abstract fun getRecipeDetailedById(id: Long): List<RecipeDetailedDb>

    @Query(GET_RECIPE_DETAILED_BY_ID)
    abstract fun observeRecipeDetailedById(id: Long): LiveData<List<RecipeDetailedDb>>

    @Query("SELECT * FROM recipes WHERE r_id=:id")
    abstract fun getRecipeById(id: Long): RecipeDb

    @Insert
    abstract fun insertRecipe(recipe: RecipeDb): Long

    @Insert
    abstract fun insertRecipes(vararg recipe: RecipeDb): LongArray

    @Update
    abstract fun updateRecipe(recipe: RecipeDb)

    @Insert
    abstract fun insertRecipeFood(recipeFood: RecipeFoodDb): Long

    @Update
    abstract fun updateRecipeFood(recipeFood: RecipeFoodDb)

    @Delete
    abstract fun deleteRecipeFood(recipeFood: RecipeFoodDb)

    @Delete
    abstract fun deleteRecipe(recipe: RecipeDb)

    @Query("SELECT * FROM recipes")
    abstract fun observeAll(): LiveData<List<RecipeDb>>

}