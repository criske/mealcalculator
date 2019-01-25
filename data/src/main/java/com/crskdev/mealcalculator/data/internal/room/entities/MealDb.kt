package com.crskdev.mealcalculator.data.internal.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.crskdev.mealcalculator.domain.entities.Carbohydrate
import com.crskdev.mealcalculator.domain.entities.Fat

/**
 * Created by Cristian Pela on 25.01.2019.
 */
@Entity(tableName = Tables.MEALS_JOURNAL)
internal class MealDb(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "m_id")
    var id: Long,
    var numberOfTheDay: Int,
    var calories: Int,
    @Embedded(prefix = "carb_")
    var carbohydrate: CarbohydrateDb,
    @Embedded(prefix = "fat_")
    var fat: FatDb,
    var protein: Float,
    var glycemicLoad: Float,
    var date: String
)
