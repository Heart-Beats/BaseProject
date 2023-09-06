package com.hl.utils.keyboard

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 手机键盘工具类
 */
object SoftKeyboardUtil {

    @JvmStatic
    fun toggleSoftInput(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, 0)
    }

    @JvmStatic
    fun hideSoftInput(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @JvmStatic
    fun showSoftInput(view: View) {
        showSoftInput(view, 0)
    }


    private fun showSoftInput(view: View, flags: Int) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        imm.showSoftInput(view, flags)
    }
}