package com.crskdev.mealcalculator.data.internal.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created by Cristian Pela on 25.01.2019.
 */
@Entity(
    tableName = "current_meal_entries",
    foreignKeys = [ForeignKey(
        entity = FoodDb::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("foodId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
internal class MealEntryDb(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var foodId: Long,
    var quantity: Int
)