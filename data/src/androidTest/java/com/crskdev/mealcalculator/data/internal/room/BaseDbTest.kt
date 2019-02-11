package com.crskdev.mealcalculator.data.internal.room

import android.content.Context
import androidx.annotation.CallSuper
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Created by Cristian Pela on 11.02.2019.
 */
@RunWith(AndroidJUnit4::class)
internal open class BaseDbTest {

    lateinit var db: MealCalculatorDatabase

    @Before
    @CallSuper
    open fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MealCalculatorDatabase::class.java).build()
    }

    @After
    @CallSuper
    @Throws(IOException::class)
    open fun tearDown() {
        db.clearAllTables()
        db.close()
    }

}