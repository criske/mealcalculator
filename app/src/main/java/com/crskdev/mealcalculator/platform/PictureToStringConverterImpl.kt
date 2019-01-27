package com.crskdev.mealcalculator.platform

import com.crskdev.mealcalculator.presentation.common.services.PictureToStringConverter
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream


/**
 * Created by Cristian Pela on 27.01.2019.
 */
class PictureToStringConverterImpl : PictureToStringConverter {

    override fun convertAbstract(bitmap: Any, sizePx: Int): String {
        require(bitmap is Bitmap)
        Bitmap.createScaledBitmap(bitmap, sizePx, sizePx, true)
        return ByteArrayOutputStream().let {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Base64.encodeToString(it.toByteArray(), Base64.DEFAULT)
        }
    }


    override fun convert(path: String, size: Int): String {
        val options = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(path, options)
        return convertAbstract(bitmap, size)
    }

    override fun covertBytes(byteArray: ByteArray, sizePx: Int): String =
        Base64.encodeToString(byteArray, Base64.DEFAULT)

    override fun toBytes(base64Img: String): ByteArray =
        Base64.decode(
            base64Img.substring(base64Img.indexOf(",") + 1),
            Base64.DEFAULT
        )
}