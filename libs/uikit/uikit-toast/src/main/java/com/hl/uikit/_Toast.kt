package com.hl.uikit

import android.app.Application
import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Context.toastInit() {
    ToastUtils.init(applicationContext as Application)
}

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT, @GravityFlag gravity: Int = Gravity.CENTER) {
    if (!ToastUtils.isInitialized) {
        toastInit()
    }
    ToastUtils.show {
        this.layout = R.layout.uikit_layout_toast
        this.text = text
        this.gravity = gravity
        this.duration = duration
    }
}

fun Context.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT, @GravityFlag gravity: Int = Gravity.CENTER) {
    toast(this.getString(resId), duration, gravity)
}

fun Fragment.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT, @GravityFlag gravity: Int = Gravity.CENTER) {
    requireContext().toast(resId, duration, gravity)
}

fun Fragment.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT, @GravityFlag gravity: Int = Gravity.CENTER) {
    requireContext().toast(text, duration, gravity)
}


fun Context.toastFailure(
    text: CharSequence,
    duration: Int = Toast.LENGTH_SHORT,
    @GravityFlag gravity: Int = Gravity.CENTER
) {
    if (!ToastUtils.isInitialized) {
        toastInit()
    }

    ToastUtils.show {
        this.layout = R.layout.uikit_layout_toast_with_icon
        this.iconRes = R.drawable.uikit_ic_toast_fail
        this.text = text
        this.gravity = gravity
        this.duration = duration
    }
}

fun Context.toastFailure(textRes: Int, duration: Int = Toast.LENGTH_SHORT, @GravityFlag gravity: Int = Gravity.CENTER) {
    toastFailure(this.getString(textRes), duration, gravity)
}

fun Fragment.toastFailure(
    textRes: Int,
    duration: Int = Toast.LENGTH_SHORT,
    @GravityFlag gravity: Int = Gravity.CENTER
) {
    requireContext().toastFailure(textRes, duration, gravity)
}

fun Fragment.toastFailure(
    text: CharSequence,
    duration: Int = Toast.LENGTH_SHORT,
    @GravityFlag gravity: Int = Gravity.CENTER
) {
    requireContext().toastFailure(text, duration, gravity)
}

fun Context.toastSuccess(
    text: CharSequence,
    duration: Int = Toast.LENGTH_SHORT,
    @GravityFlag gravity: Int = Gravity.CENTER
) {
    if (!ToastUtils.isInitialized) {
        toastInit()
    }
    ToastUtils.show {
        this.layout = R.layout.uikit_layout_toast_with_icon
        this.iconRes = R.drawable.uikit_ic_toast_success
        this.text = text
        this.gravity = gravity
        this.duration = duration
    }
}

fun Context.toastSuccess(textRes: Int, duration: Int = Toast.LENGTH_SHORT, @GravityFlag gravity: Int = Gravity.CENTER) {
    toastSuccess(this.getString(textRes), duration, gravity)
}

fun Fragment.toastSuccess(
    text: CharSequence,
    duration: Int = Toast.LENGTH_SHORT,
    @GravityFlag gravity: Int = Gravity.CENTER
) {
    requireContext().toastSuccess(text, duration, gravity)
}

fun Fragment.toastSuccess(
    textRes: Int,
    duration: Int = Toast.LENGTH_SHORT,
    @GravityFlag gravity: Int = Gravity.CENTER
) {
    requireContext().toastSuccess(textRes, duration, gravity)
}