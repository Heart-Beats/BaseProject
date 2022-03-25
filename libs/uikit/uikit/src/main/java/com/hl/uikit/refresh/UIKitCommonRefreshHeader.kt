package com.hl.uikit.refresh

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.hl.uikit.R

/**
 * @Author  张磊  on  2021/01/12 at 11:44
 * Email: 913305160@qq.com
 */
open class UIKitCommonRefreshHeader @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : UIKitLottieRefreshHeaderFooter(context, attrs, defStyleAttr) {

    private companion object {
        private const val TAG = "CommonRefreshHeader"
    }

    var initBottomOffset = 0

    override val headerOrFooterLayout: Int
        get() = R.layout.uikit_layout_lottie_refresh_header

    override val hasLottieAnimationView: Boolean
        get() = true

    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
        Log.d(TAG, "onMoving: 正在下拉中==$isDragging, 百分比 == $percent, 偏移 == $offset, 头部高度 == $height, 最大拖动高度 ==$maxDragHeight")

        Log.d(TAG, "onMoving: lottieAnimationView 的大小 ----> width:${lottieAnimationView.width}, " +
                    "height:${lottieAnimationView.height},   距离顶部距离 == ${lottieAnimationView.top}"
        )
        ensureLottieAnimationView {
            //isDragging 表示下拉
            if (isDragging) {

                //下拉至 lottieAnimationView 顶部的距离
                val offsetBottom =  height + initBottomOffset - lottieAnimationView.top

                //lottieAnimationView 的顶部显示后还可以拖拽的距离(需要减去底部初始偏移的距离)
                val maxCalculateOffset = lottieAnimationView.top - initBottomOffset

                val newOffset = offset - offsetBottom
                val animationProgress = if (!pullAnimationSource.isCanUse()) {
                    //下拉动画不可用时
                    offset.run {
                        if (this <= height) {
                            //下拉未超过头布局时
                            this / height.toFloat()
                        } else 1f
                    }
                } else {
                    //下拉动画可用
                    if (newOffset > 0) {
                        // 下拉超过 lottieAnimationView 顶部时
                        newOffset / maxCalculateOffset.toFloat()
                    } else 0f
                }

                Log.d(TAG, "onMoving: 当前动画进度 == $animationProgress")
                lottieAnimationView.visibility = if (animationProgress <= 0) View.INVISIBLE else View.VISIBLE
                lottieAnimationView.progress = animationProgress
            }
        }
    }
}