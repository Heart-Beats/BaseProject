package com.hl.utils.keyboard

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.*
import android.widget.PopupWindow
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @author  张磊  on  2022/02/22 at 21:42
 * Email: 913305160@qq.com
 *
 *  使用时需要 ：Activity 的 windowSoftInputMode设置为 adjustNothing
 *
 *   原理： 在 Activity 中创建一个高度为 match_parent，宽度为 0 的 PopupWindow， 通过 PopupWindow 获取软件盘弹起的高度
 */
class SoftKeyboardHeightProvider(val activity: FragmentActivity) : PopupWindow(activity),
    ViewTreeObserver.OnGlobalLayoutListener {

    private var maxHeight = 0
    private var isSoftKeyboardOpened = false

    var onKeyboardStateListener: OnKeyboardStateListener? = null


    init {
        val contentView = View(activity)
        setContentView(contentView)
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        inputMethodMode = INPUT_METHOD_NEEDED

        contentView.viewTreeObserver.addOnGlobalLayoutListener(this)

        if (!isShowing) {
            val decorView = activity.window?.decorView
            decorView?.post {
                showAtLocation(decorView, Gravity.NO_GRAVITY, 0, 0)
            }
        }

        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                contentView.viewTreeObserver.addOnGlobalLayoutListener(this@SoftKeyboardHeightProvider)
            }

            override fun onStop(owner: LifecycleOwner) {
                contentView.viewTreeObserver.removeOnGlobalLayoutListener(this@SoftKeyboardHeightProvider)
            }
        })
    }


    override fun onGlobalLayout() {
        val rect = Rect()
        contentView.getWindowVisibleDisplayFrame(rect)
        if (rect.bottom > maxHeight) {
            maxHeight = rect.bottom
        }
        val screenHeight: Int = getScreenHeight(activity)
        //键盘的高度
        val keyboardHeight = maxHeight - rect.bottom
        val visible = keyboardHeight > screenHeight / 4
        if (!isSoftKeyboardOpened && visible) {
            isSoftKeyboardOpened = true
            onKeyboardStateListener?.onOpened(keyboardHeight)
        } else if (isSoftKeyboardOpened && !visible) {
            isSoftKeyboardOpened = false
            onKeyboardStateListener?.onClosed()
        }
    }

    private fun getDisplayMetrics(context: Context): DisplayMetrics {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics
    }

    /**
     * 获取屏幕高度
     * @return
     */
    private fun getScreenHeight(context: Context): Int {
        return getDisplayMetrics(context).heightPixels
    }


    /**
     * 通用软键盘弹起处理
     *
     * 弹起时会将根布局的 paddingBottom 设为软键盘的高度，关闭时恢复根布局原始状态
     */
    fun initCommonKeyboardStateHandle() {
        val keyboardStateListener = object : OnKeyboardStateListener {

            private var originViewBottomPadding: Int = 0

            private val rootView = activity.findViewById<View>(android.R.id.content).apply {
                post { originViewBottomPadding = this.paddingBottom }
            }

            override fun onOpened(keyboardHeight: Int) {
                rootView.run {
                    // 软键盘弹出时更新  paddingBottom
                    post { updatePadding(bottom = rootView.paddingBottom + keyboardHeight) }
                }
            }

            override fun onClosed() {
                rootView.run {
                    // 软键盘关闭时恢复原始 paddingBottom
                    post { updatePadding(bottom = originViewBottomPadding) }
                }
            }
        }
        this.onKeyboardStateListener = keyboardStateListener
    }

    /**
     * 移除全局软键盘处理
     */
    fun removeCommonKeyboardStateHandle() {
        this.onKeyboardStateListener = null
    }


    interface OnKeyboardStateListener {

        /**
         * 软键盘打开时回调
         *
         * @param keyboardHeight 软键盘打开时的高度
         */
        fun onOpened(keyboardHeight: Int)

        /**
         * 软键盘关闭时回调
         */
        fun onClosed()
    }

}