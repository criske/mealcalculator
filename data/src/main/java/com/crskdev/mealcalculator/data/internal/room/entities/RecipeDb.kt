package com.crskdev.mealcalculator.data.internal.room.entities

import androidx.room.*

/**
 * Created by Cristian Pela on 11.02.2019.
 */
@Entity(tableName = Tables.RECIPES)
internal data class RecipeDb(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "r_id")
    val id: Long,
    @ColumnInfo(name = "r_name")
    val name: String
)

@Entity(
    tableName = Tables.RECIPES_FOODS,
    foreignKeys =
    [
        ForeignKey(
            entity = FoodDb::class,
            parentColumns = arrayOf("food_id"),
            childColumns = arrayOf("rf_fk_f_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecipeDb::class,
            parentColumns = arrayOf("r_id"),
            childColumns = arrayOf("rf_fk_r_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
internal data class RecipeFoodDb(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rf_id")
    val id: Long,
    @ColumnInfo(name = "rf_fk_r_id")
    val recipeId: Long,
    @ColumnInfo(name = "rf_fk_f_id")
    val foodId: Long,
    @ColumnInfo(name = "rf_quantity")
    val quantity: Int
)


internal class RecipeDetailedDb(
    @ColumnInfo(name = "rf_id")
    val id: Long,
    @Embedded
    val recipe: RecipeDb,
    @ColumnInfo(name = "rf_quantity")
    val quantity: Int,
    @Embedded
    val food: FoodDb
)