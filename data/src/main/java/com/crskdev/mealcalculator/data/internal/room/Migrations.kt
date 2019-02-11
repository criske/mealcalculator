package com.crskdev.mealcalculator.data.internal.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Created by Cristian Pela on 11.02.2019.
 */
internal object Migrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            with(database) {
                execSQL("CREATE TABLE IF NOT EXISTS `recipes` (`r_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `r_name` TEXT NOT NULL)")
                execSQL("CREATE TABLE IF NOT EXISTS `recipes_foods` (`rf_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `rf_fk_r_id` INTEGER NOT NULL, `rf_fk_f_id` INTEGER NOT NULL, `rf_quantity` INTEGER NOT NULL, FOREIGN KEY(`rf_fk_f_id`) REFERENCES `foods`(`food_id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`rf_fk_r_id`) REFERENCES `recipes`(`r_id`) ON UPDATE CASCADE ON DELETE CASCADE )")
            }
        }
    }

}