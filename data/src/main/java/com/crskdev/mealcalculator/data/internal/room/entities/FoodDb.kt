package com.crskdev.mealcalculator.data.internal.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat

/**
 * Created by Cristian Pela on 25.01.2019.
 */

@Entity(tableName = "foods")
internal class FoodDb(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var name: String,
    var picture: String?,
    var calories: Int,
    @Embedded(prefix = "carbs_")
    var carbohydrate: Carbohydrate,
    @Embedded(prefix = "fat_")
    var fat: Fat,
    var proteins: Float,
    var gi: Int = 0
)