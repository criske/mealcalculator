package com.crskdev.mealcalculator.data.internal.room

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Cristian Pela on 11.02.2019.
 */
@RunWith(AndroidJUnit4::class)
internal class MealCalculatorDatabaseTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Rule
    @JvmField
    var migrationTestHelper = MigrationTestHelper(
        instrumentation,
        MealCalculatorDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    lateinit var sqLiteTestOpenHelper: SQLiteTestOpenHelper
    lateinit var sqLiteTestDataHelper: SQLiteTestDataHelper

    @Before
    fun setup() {
        sqLiteTestOpenHelper =
            SQLiteTestOpenHelper(instrumentation.context, MealCalculatorDatabase.NAME)
        sqLiteTestDataHelper = SQLiteTestDataHelper(sqLiteTestOpenHelper)
    }

    @After
    fun tearDown() {
        sqLiteTestDataHelper.clearDatabase()
        sqLiteTestOpenHelper.close()
    }

    @Test
    fun test1Plus1() {
        assertEquals(2, 1 + 1)
    }


}