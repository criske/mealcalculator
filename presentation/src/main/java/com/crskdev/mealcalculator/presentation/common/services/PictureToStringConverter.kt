package com.crskdev.mealcalculator.presentation.common.services

/**
 * Created by Cristian Pela on 26.01.2019.
 */
interface PictureToStringConverter {

    fun convertAbstract(bitmap: Any, sizePx: Int): String

    fun convert(path: String, sizePx: Int): String

    fun covertBytes(byteArray: ByteArray, sizePx: Int): String

    fun toBytes(base64Img: String): ByteArray

}