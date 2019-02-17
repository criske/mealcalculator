package com.crskdev.mealcalculator.data

import com.crskdev.mealcalculator.data.internal.room.MealCalculatorDatabase
import com.crskdev.mealcalculator.data.internal.room.entities.toDb
import com.crskdev.mealcalculator.data.internal.room.entities.toDomain
import com.crskdev.mealcalculator.data.internal.runTransactionDelegate
import com.crskdev.mealcalculator.data.internal.utils.toChannel
import com.crskdev.mealcalculator.domain.entities.Recipe
import com.crskdev.mealcalculator.domain.entities.RecipeDetailed
import com.crskdev.mealcalculator.domain.entities.RecipeFood
import com.crskdev.mealcalculator.domain.gateway.RecipeRepository

/**
 * Created by Cristian Pela on 11.02.2019.
 */
class RecipeRepositoryImpl(private val db: MealCalculatorDatabase) : RecipeRepository {

    private val dao by lazy {
        db.recipeDao()
    }

    override fun save(recipe: Recipe): Long = dao.insertRecipe(recipe.toDb())

    override fun getRecipeById(id: Long): RecipeDetailed? =
        dao.getRecipeDetailedById(id)
            .takeIf { it.isNotEmpty() }
            ?.toDomain()

    override fun delete(recipe: Recipe) =
        dao.deleteRecipe(recipe.toDb())

    override fun update(recipe: Recipe) =
        dao.updateRecipe(recipe.toDb())

    override fun removeRecipeFood(recipeId: Long, food: RecipeFood) =
        dao.deleteRecipeFood(food.toDb(recipeId))


    override fun addRecipeFood(recipeId: Long, food: RecipeFood) =
        dao.insertRecipeFood(food.toDb(recipeId))

    override fun updateRecipeFood(recipeId: Long, food: RecipeFood) =
        dao.updateRecipeFood(food.toDb(recipeId))

    override suspend fun observeAll(observer: (List<Recipe>) -> Unit) =
        dao.observeAllWithFoodNames().toChannel { ch ->
            for (list in ch) {
                observer(list.map { it.toDomain() })
            }
        }

    override suspend fun observeRecipe(id: Long, observer: (RecipeDetailed) -> Unit) =
        dao.observeRecipeDetailedById(id).toChannel { ch ->
            for (list in ch) {
                observer(list.toDomain())
            }
        }

    override fun runTransaction(block: RecipeRepository.() -> Unit) =
        runTransactionDelegate(db, block)


}