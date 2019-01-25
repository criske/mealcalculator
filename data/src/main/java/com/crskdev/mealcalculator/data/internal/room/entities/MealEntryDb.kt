package com.crskdev.mealcalculator.data.internal.room.entities

import androidx.room.*

/**
 * Created by Cristian Pela on 25.01.2019.
 */
@Entity(
    tableName = Tables.CURRENT_MEAL_ENTRIES,
    foreignKeys =
    [
        ForeignKey(
            entity = FoodDb::class,
            parentColumns = arrayOf("food_id"),
            childColumns = arrayOf("fk_food_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MealDb::class,
            parentColumns = arrayOf("m_id"),
            childColumns = arrayOf("fk_m_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )

    ],
    indices = [
        Index("fk_m_id", unique = true)
    ]
)
internal class MealEntryDb(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "me_id")
    var id: Long,
    @ColumnInfo(name = "fk_m_id")
    val mealId: Long,
    val mealDate: String,
    val mealNumber: Int,
    @ColumnInfo(name = "fk_food_id")
    var foodId: Long,
    var quantity: Int
)

internal class MealEntryWithFoodDb(
    @Embedded
    var mealEntry: MealEntryDb,
    @Embedded
    var food: FoodDb
)