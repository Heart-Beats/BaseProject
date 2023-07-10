package com.hl.uikit.dialog

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * @author  张磊  on  2021/11/08 at 11:43
 * Email: 913305160@qq.com
 */

fun DialogFragment.showInFragment(fragment: Fragment, tag: String = this.javaClass.simpleName) {
    show(fragment.childFragmentManager, tag)
}

fun DialogFragment.showInActivity(activity: FragmentActivity, tag: String = this.javaClass.simpleName) {
    this.show(activity.supportFragmentManager, tag)
}

fun FragmentActivity.popConfirmDialog(
    dialogTitle: String? = null, dialogMessage: CharSequence, confirmText: CharSequence = "确认",
    onConfirmListener: () -> Unit = {}
): AlertDialogFragment {
    val dialogFragment = AlertDialogFragment().apply {
        if (dialogTitle != null) {
            setTitle(dialogTitle)
        }
        setMessage(dialogMessage)
        setPositiveButton(confirmText) { _, _ ->
            dismiss()
            onConfirmListener()
        }
    }
    dialogFragment.showInActivity(this)
    return dialogFragment
}

fun FragmentActivity.popConfirmAndCancelDialog(
    dialogTitle: String? = null, dialogMessage: CharSequence,
    cancelText: CharSequence = "取消",
    onCancelListener: () -> Unit = {},
    confirmText: CharSequence = "确认",
    onConfirmListener: () -> Unit = {}
): AlertDialogFragment {
    val dialogFragment = AlertDialogFragment().apply {
        if (dialogTitle != null) {
            setTitle(dialogTitle)
        }
        setMessage(dialogMessage)
        setNegativeButton(cancelText) { _, _ ->
            dismiss()
            onCancelListener()
        }
        setPositiveButton(confirmText) { _, _ ->
            dismiss()
            onConfirmListener()
        }
    }
    dialogFragment.showInActivity(this)
    return dialogFragment
}

fun Fragment.popConfirmDialog(
    dialogTitle: String? = null, dialogMessage: CharSequence, confirmText: CharSequence = "确认",
    onConfirmListener: () -> Unit = {}
): AlertDialogFragment {
    val dialogFragment = AlertDialogFragment().apply {
        if (dialogTitle != null) {
            setTitle(dialogTitle)
        }
        setMessage(dialogMessage)
        setPositiveButton(confirmText) { _, _ ->
            dismiss()
            onConfirmListener()
        }
    }
    dialogFragment.showInFragment(this)
    return dialogFragment
}

fun Fragment.popConfirmAndCancelDialog(
    dialogTitle: String? = null, dialogMessage: CharSequence,
    cancelText: CharSequence = "取消",
    onCancelListener: () -> Unit = {},
    confirmText: CharSequence = "确认",
    onConfirmListener: () -> Unit = {}
): AlertDialogFragment {
    val dialogFragment = AlertDialogFragment().apply {
        if (dialogTitle != null) {
            setTitle(dialogTitle)
        }
        setMessage(dialogMessage)
        setNegativeButton(cancelText) { _, _ ->
            dismiss()
            onCancelListener()
        }
        setPositiveButton(confirmText) { _, _ ->
            dismiss()
            onConfirmListener()
        }
    }
    dialogFragment.showInFragment(this)
    return dialogFragment
}