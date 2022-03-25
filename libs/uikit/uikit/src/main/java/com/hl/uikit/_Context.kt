package com.hl.uikit

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    if (!ToastUtils.isInitialized) {
        ToastUtils.init(requireContext().applicationContext)
    }
    ToastUtils.show(textRes = resId, duration = duration)
}

fun Fragment.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    if (!ToastUtils.isInitialized) {
        ToastUtils.init(requireContext().applicationContext)
    }
    ToastUtils.show(text, duration)
}

fun Context.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    if (!ToastUtils.isInitialized) {
        ToastUtils.init(applicationContext)
    }
    ToastUtils.show(textRes = resId, duration = duration)
}

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    if (!ToastUtils.isInitialized) {
        ToastUtils.init(applicationContext)
    }
    ToastUtils.show(text, duration)
}

fun Fragment.toastFailure(textRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toastFailure(textRes, duration)
}

fun Context.toastFailure(textRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    if (!ToastUtils.isInitialized) {
        ToastUtils.init(applicationContext)
    }
    ToastUtils.show(
        iconRes = R.drawable.uikit_ic_toast_fail,
        textRes = textRes,
        duration = duration
    )
}

fun Fragment.toastFailure(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toastFailure(text, duration)
}

fun Context.toastFailure(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    if (!ToastUtils.isInitialized) {
        ToastUtils.init(applicationContext)
    }
    ToastUtils.show(iconRes = R.drawable.uikit_ic_toast_fail, text = text, duration = duration)
}

fun Fragment.toastSuccess(textRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toastSuccess(textRes, duration)
}

fun Context.toastSuccess(textRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    if (!ToastUtils.isInitialized) {
        ToastUtils.init(applicationContext)
    }
    ToastUtils.show(
        iconRes = R.drawable.uikit_ic_toast_success,
        textRes = textRes,
        duration = duration
    )
}

fun Fragment.toastSuccess(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toastSuccess(text, duration)
}

fun Context.toastSuccess(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    if (!ToastUtils.isInitialized) {
        ToastUtils.init(applicationContext)
    }
    ToastUtils.show(iconRes = R.drawable.uikit_ic_toast_success, text = text, duration = duration)
}


fun Any.toast(text: CharSequence, duration: Int) {
    ToastUtils.show(text, duration)
}

fun Context.toastInit() {
    ToastUtils.init(applicationContext)
}