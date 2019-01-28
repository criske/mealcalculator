package com.crskdev.mealcalculator.utils

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.TypedValue
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory

/**
 * Created by Cristian Pela on 26.01.2019.
 */
fun Float.dpToPx(resources: Resources): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics)


fun Int.dpToPx(resources: Resources): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics)


object ProjectDrawableUtils {
    fun convertStrToRoundedDrawable(resources: Resources, base64Str: String, radius: Float = 5f): Drawable {
        val decodedBytes = Base64.decode(
            base64Str.substring(base64Str.indexOf(",") + 1),
            Base64.DEFAULT
        )
        return RoundedBitmapDrawableFactory.create(
            resources,
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        ).apply {
            cornerRadius = radius.dpToPx(resources)
        }
    }
}

