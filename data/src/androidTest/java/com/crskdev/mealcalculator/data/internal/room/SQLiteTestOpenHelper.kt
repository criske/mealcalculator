package com.crskdev.mealcalculator.data.internal.room

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.crskdev.mealcalculator.data.internal.room.entities.FoodDb
import com.crskdev.mealcalculator.data.internal.room.entities.Tables


/**
 * Created by Cristian Pela on 11.02.2019.
 */
internal class SQLiteTestOpenHelper(context: Context, databaseName: String) :

    SQLiteOpenHelper(context, databaseName, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `foods` (`food_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `picture` TEXT, `calories` INTEGER NOT NULL, `proteins` REAL NOT NULL, `gi` INTEGER NOT NULL, `carbs_total` REAL NOT NULL, `carbs_fiber` REAL NOT NULL, `carbs_sugar` REAL NOT NULL, `fat_total` REAL NOT NULL, `fat_saturated` REAL NOT NULL, `fat_unsaturated` REAL NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `meals_journal` (`m_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `numberOfTheDay` INTEGER NOT NULL, `calories` INTEGER NOT NULL, `protein` REAL NOT NULL, `glycemicLoad` REAL NOT NULL, `date` TEXT NOT NULL, `carb_total` REAL NOT NULL, `carb_fiber` REAL NOT NULL, `carb_sugar` REAL NOT NULL, `fat_total` REAL NOT NULL, `fat_saturated` REAL NOT NULL, `fat_unsaturated` REAL NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `current_meal_entries` (`me_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fk_m_id` INTEGER NOT NULL, `mealDate` TEXT NOT NULL, `mealNumber` INTEGER NOT NULL, `fk_food_id` INTEGER NOT NULL, `quantity` INTEGER NOT NULL, FOREIGN KEY(`fk_food_id`) REFERENCES `foods`(`food_id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`fk_m_id`) REFERENCES `meals_journal`(`m_id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `recipes` (`r_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `r_name` TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `recipes_foods` (`rf_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `rf_fk_r_id` INTEGER NOT NULL, `rf_fk_f_id` INTEGER NOT NULL, `rf_quantity` INTEGER NOT NULL, FOREIGN KEY(`rf_fk_f_id`) REFERENCES `foods`(`food_id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`rf_fk_r_id`) REFERENCES `recipes`(`r_id`) ON UPDATE CASCADE ON DELETE CASCADE )")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Not required as at version 1
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Not required as at version 1
    }

    companion object {
        val DATABASE_VERSION = 1
    }
}

internal class SQLiteTestDataHelper(
    private val helper: SQLiteTestOpenHelper) {

    fun insertFood(food: FoodDb) {

    }

    fun clearDatabase() {
        val dropTable: SQLiteDatabase.() -> (String) -> Unit = {
            {
                execSQL("DROP TABLE IF EXISTS $it")
            }
        }
        helper.writableDatabase.use { db ->
            val drop = db.dropTable()
            Tables.forEach {
                drop(it)
            }
        }
    }
}