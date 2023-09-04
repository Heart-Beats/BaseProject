package com.hl.uikit

import android.content.DialogInterface
import androidx.fragment.app.DialogFragment

abstract class BasicDialogFragment : DialogFragment() {
    var onDismissListener: (dialog: DialogInterface) -> Unit = {}
    var onCancelListener: (dialog: DialogInterface) -> Unit = {}

    /**
     * Fragment 对应的布局是否已创建
     */
    protected fun isRootViewCreated() = view != null

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCancelListener(dialog)
    }
}