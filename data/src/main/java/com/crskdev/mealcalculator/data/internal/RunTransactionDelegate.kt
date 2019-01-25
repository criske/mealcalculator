package com.crskdev.mealcalculator.data.internal

import androidx.room.RoomDatabase
import com.crskdev.mealcalculator.domain.gateway.Transactionable

/**
 * Created by Cristian Pela on 25.01.2019.
 */
internal fun <T : Transactionable<*>> T.runTransactionDelegate(db: RoomDatabase, block: T.() -> Unit) {
    if (db.inTransaction()) {
        block()
    } else {
        db.beginTransaction()
        try {
            block()
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            throw ex
        } finally {
            db.endTransaction()
        }
    }
}