package com.crskdev.mealcalculator.data.internal.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.crskdev.mealcalculator.data.internal.room.entities.*
import java.util.concurrent.Executors

/**
 * Created by Cristian Pela on 25.01.2019.
 */
@Database(
    exportSchema = false,
    version = 2,
    entities = [FoodDb::class, MealDb::class, MealEntryDb::class, RecipeDb::class, RecipeFoodDb::class]
)
abstract class MealCalculatorDatabase : RoomDatabase() {

    internal abstract fun foodDao(): FoodDao

    internal abstract fun mealDao(): MealDao

    internal abstract fun mealEntryDao(): MealEntryDao

    internal abstract fun recipeDao(): RecipeDao

    companion object {

        internal const val NAME = "meal-calculator.db"

        @Volatile
        @PublishedApi
        internal var INSTANCE: MealCalculatorDatabase? = null

        fun inMemory(context: Context, block: (MealCalculatorDatabase) -> Unit = {}): MealCalculatorDatabase =
            INSTANCE ?: synchronized(MealCalculatorDatabase::class.java) {
                INSTANCE ?: buildDatabase(context, null, block).also { INSTANCE = it }
            }

        fun persistent(context: Context, block: (MealCalculatorDatabase) -> Unit = {}): MealCalculatorDatabase =
            INSTANCE ?: synchronized(MealCalculatorDatabase::class.java) {
                INSTANCE ?: buildDatabase(context, NAME, block).also {
                    INSTANCE = it
                }
            }

        private fun buildDatabase(context: Context, name: String?, block: (MealCalculatorDatabase) -> Unit): MealCalculatorDatabase {
            val executor = Executors.newSingleThreadExecutor()
            return if (name == null) {
                Room.inMemoryDatabaseBuilder(context, MealCalculatorDatabase::class.java)
                    .build()
                    .apply {
                        executor.execute {
                            block(this)
                        }
                    }
            } else {
                Room.databaseBuilder(context, MealCalculatorDatabase::class.java, name)
                    .addMigrations(*Migrations())
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            executor.execute {
                                val persistentDb = persistent(context, block)
                                block(persistentDb)
                            }
                        }
                    }).build()
            }
        }
    }
}