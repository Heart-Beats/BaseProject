package com.hl.utils.keyboard

import android.graphics.Rect
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @author  张磊  on  2022/02/22 at 21:25
 * Email: 913305160@qq.com
 *
 * 解决 WebView 全屏模式 / 沉浸式标题栏下， windowSoftInputMode 设置为 adjustPan 和 adjustResize 无效的 bug
 */
class AndroidBug5497Workaround constructor(activity: FragmentActivity) {

    private val contentContainer = activity.findViewById(android.R.id.content) as ViewGroup
    private val rootView = contentContainer.getChildAt(0)
    private val rootViewLayout = rootView.layoutParams as FrameLayout.LayoutParams
    private val viewTreeObserver = rootView.viewTreeObserver
    private val listener = ViewTreeObserver.OnGlobalLayoutListener { possiblyResizeChildOfContent() }

    private val contentAreaOfWindowBounds = Rect()
    private var usableHeightPrevious = 0

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                viewTreeObserver.addOnGlobalLayoutListener(listener)
            }

            override fun onStop(owner: LifecycleOwner) {
                viewTreeObserver.removeOnGlobalLayoutListener(listener)
            }
        })
    }

    private fun possiblyResizeChildOfContent() {
        contentContainer.getWindowVisibleDisplayFrame(contentAreaOfWindowBounds)
        val usableHeightNow = contentAreaOfWindowBounds.height()
        if (usableHeightNow != usableHeightPrevious) {
            rootViewLayout.height = usableHeightNow
            // Change the bounds of the root view to prevent gap between keyboard and content, and top of content positioned above top screen edge.
            rootView.layout(
                contentAreaOfWindowBounds.left,
                contentAreaOfWindowBounds.top,
                contentAreaOfWindowBounds.right,
                contentAreaOfWindowBounds.bottom
            )
            rootView.requestLayout()

            usableHeightPrevious = usableHeightNow
        }
    }
}