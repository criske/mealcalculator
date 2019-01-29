package com.crskdev.mealcalculator.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.postDelayed
import com.crskdev.mealcalculator.presentation.common.utils.cast
import java.io.ByteArrayOutputStream

/**
 * Created by Cristian Pela on 26.01.2019.
 */
fun Float.dpToPx(resources: Resources): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics)


fun Int.dpToPx(resources: Resources): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics)


object ProjectImageUtils {

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

    fun convertBitmapToBase64String(bitmap: Bitmap, sizePx: Int): String {
        Bitmap.createScaledBitmap(bitmap, sizePx, sizePx, true)
        return ByteArrayOutputStream().let {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Base64.encodeToString(it.toByteArray(), Base64.DEFAULT)
        }
    }
}

fun AppCompatActivity.onBackPressedToExit(
    message: String = "Please click BACK again to exit",
    isUsingNavHostFragment: Boolean = false,
    delayMs: Long = 2000): Boolean {
    val fragmentManager = if (isUsingNavHostFragment) {
        supportFragmentManager
            .fragments
            .first()
            .childFragmentManager
    } else {
        supportFragmentManager
    }
    val isAboutToExit =
        !(window.decorView.tag?.cast<Boolean>() ?: false)
                && (fragmentManager.backStackEntryCount == 0)
    if (isAboutToExit) {
        showSimpleToast(message)
        with(window.decorView) {
            tag = true
            postDelayed(delayMs) {
                tag = false
            }
            Unit
        }
    }
    return isAboutToExit
}

inline fun Context.simpleAlertDialog(title: String, msg: String, crossinline onConfirm: () -> Unit): AlertDialog.Builder =
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(msg)
        .setCancelable(true)
        .setPositiveButton(android.R.string.ok) { d, _ ->
            onConfirm()
            d.dismiss()
        }

inline fun Context.showSimpleAlertDialog(title: String, msg: String, crossinline onConfirm: () -> Unit) {
    simpleAlertDialog(title, msg, onConfirm).show()
}

fun Context.showSimpleToast(title: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, title, duration).show()
}


inline fun Context.simpleYesNoDialog(title: String, msg: String, crossinline onSelect: (Int) -> Unit): AlertDialog.Builder =
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(msg)
        .setCancelable(false)
        .setNegativeButton(android.R.string.no) { d, which ->
            onSelect(which)
            d.dismiss()
        }
        .setPositiveButton(android.R.string.ok) { d, which ->
            onSelect(which)
            d.dismiss()
        }

inline fun Context.showSimpleYesNoDialog(title: String, msg: String, crossinline onSelect: (Int) -> Unit) {
    simpleYesNoDialog(title, msg, onSelect).create().show()
}
