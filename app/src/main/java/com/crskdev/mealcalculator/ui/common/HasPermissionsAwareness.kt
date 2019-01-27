package com.crskdev.mealcalculator.ui.common

/**
 * Created by Cristian Pela on 27.01.2019.
 */
interface HasPermissionsAwareness{

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)

}