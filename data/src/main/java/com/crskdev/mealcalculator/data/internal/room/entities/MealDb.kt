package com.crskdev.mealcalculator.data.internal.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat

/**
 * Created by Cristian Pela on 25.01.2019.
 */
/*
Meal(var id: Int = -1,
     var numberOfTheDay: Int =
     var calories: Int,
     var carbohydrate: Carbohydrate
     var fat: Fat,
     var protein: Float,
     var glycemicLoad: Float,
     var date: String? = null)
 */
@Entity(tableName = "meals_journal")
internal class MealDb(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var numberOfTheDay: Int,
    var calories: Int,
    @Embedded(prefix = "carb_")
    var carbohydrate: Carbohydrate,
    @Embedded(prefix = "fat_")
    var fat: Fat,
    var protein: Float,
    var glycemicLoad: Float,
    var date: String? = null
)