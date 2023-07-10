package com.hl.uikit

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.GravityInt
import androidx.fragment.app.Fragment


fun Context.showShortToast(message: String, @GravityInt gravity: Int = Gravity.CENTER) {
    toast(message, gravity = gravity)
}

fun Context.showLongToast(message: String, @GravityInt gravity: Int = Gravity.CENTER) {
    toast(message, Toast.LENGTH_LONG, gravity = gravity)
}

fun Context.showShortError(message: String, @GravityInt gravity: Int = Gravity.CENTER) {
    toastFailure(message, gravity = gravity)
}

fun Context.showLongError(message: String, @GravityInt gravity: Int = Gravity.CENTER) {
    toastFailure(message, Toast.LENGTH_LONG, gravity = gravity)
}

fun Fragment.showShortToast(message: String) {
    requireContext().showShortToast(message)
}

fun Fragment.showLongToast(message: String) {
    requireContext().showLongToast(message)
}

fun Fragment.showShortError(message: String) {
    requireContext().showShortError(message)
}

fun Fragment.showLongError(message: String) {
    requireContext().showLongError(message)
}