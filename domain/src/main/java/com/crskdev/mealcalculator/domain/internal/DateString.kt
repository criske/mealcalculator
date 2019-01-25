package com.crskdev.mealcalculator.domain.internal

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Cristian Pela on 25.01.2019.
 */
internal object DateString {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

    fun obtain(): String = dateFormatter.format(Date())

    operator fun invoke(): String = obtain()
}