package com.hl.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.GravityInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.shashank.sony.fancytoastlib.FancyToast

/**
 * @author sam
 * @version 1.0
 * @date 2/1/2018
 */
private var toast: Toast? = null

fun Context.showShortToast(message: String, @GravityInt gravity: Int = Gravity.CENTER) {
    toast?.cancel()

    FancyToast.makeText(this, message, FancyToast.LENGTH_SHORT, FancyToast.INFO, false).apply {
        setGravity(gravity, 0, 0)
    }.also {
        toast = it
    }.show()
}

fun Context.showLongToast(message: String, @GravityInt gravity: Int = Gravity.CENTER) {
    toast?.cancel()

    FancyToast.makeText(this, message, FancyToast.LENGTH_LONG, FancyToast.INFO, false).apply {
        setGravity(gravity, 0, 0)
    }.also {
        toast = it
    }.show()
}

fun Context.showShortError(message: String, @GravityInt gravity: Int = Gravity.CENTER) {
    toast?.cancel()

    FancyToast.makeText(this, message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).apply {
        setGravity(gravity, 0, 0)
    }.also {
        toast = it
    }.show()
}

fun Context.showLongError(message: String, @GravityInt gravity: Int = Gravity.CENTER) {
    toast?.cancel()

    FancyToast.makeText(this, message, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).apply {
        setGravity(gravity, 0, 0)
    }.also {
        toast = it
    }.show()
}

fun FragmentActivity.showShortToast(message: String) {
    baseContext.showShortToast(message)
}

fun FragmentActivity.showLongToast(message: String) {
    baseContext.showLongToast(message)
}

fun Fragment.showShortToast(message: String) {
    requireContext().showShortToast(message)
}

fun Fragment.showLongToast(message: String) {
    requireContext().showLongToast(message)
}

fun FragmentActivity.showShortError(message: String) {
    baseContext.showShortError(message)
}

fun FragmentActivity.showLongError(message: String) {
    baseContext.showLongError(message)
}

fun Fragment.showShortError(message: String) {
    requireContext().showShortError(message)
}

fun Fragment.showLongError(message: String) {
    requireContext().showLongError(message)
}