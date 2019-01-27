package com.crskdev.mealcalculator.data.internal.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.crskdev.mealcalculator.data.internal.room.entities.FoodDb
import com.crskdev.mealcalculator.data.internal.room.entities.MealDb
import com.crskdev.mealcalculator.data.internal.room.entities.MealEntryDb

/**
 * Created by Cristian Pela on 25.01.2019.
 */
@Database(
    exportSchema = false,
    version = 1,
    entities = [FoodDb::class, MealDb::class, MealEntryDb::class]
)
abstract class MealCalculatorDatabase : RoomDatabase() {

    internal abstract fun foodDao(): FoodDao

    internal abstract fun mealDao(): MealDao

    internal abstract fun mealEntryDao(): MealEntryDao

    companion object {
        fun inMemory(context: Context): MealCalculatorDatabase =
            Room.inMemoryDatabaseBuilder(context, MealCalculatorDatabase::class.java).build()
        fun persistent(context: Context): MealCalculatorDatabase = TODO()
    }
}