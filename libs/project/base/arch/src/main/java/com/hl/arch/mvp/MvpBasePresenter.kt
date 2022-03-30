package com.hl.arch.mvp

import android.app.Activity
import androidx.fragment.app.Fragment

/**
 * Author: txwang
 * Version: V1.0版本
 * Description:基类presenter
 * Date: 2019/3/5
 */
abstract class MvpBasePresenter<View> {

    protected val TAG = this.javaClass.simpleName

    protected var view: View? = null
        private set

    fun detachView(view: Any) {
        this.view = view as? View
    }

    open fun unDetachView() {
        view = null
    }

    fun hasView(): Boolean {
        return when (view) {
            null -> {
                false
            }
            is Activity -> {
                !(view as Activity).isDestroyed
            }
            is Fragment -> {
                !((view as Fragment).isDetached || (view as Fragment).context == null)
            }
            else -> null != view
        }
    }
}


