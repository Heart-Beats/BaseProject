package com.hl.uikit.refresh

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import com.hl.uikit.R
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState

/**
 * @Author  张磊  on  2021/01/18 at 11:23
 * Email: 913305160@qq.com
 */
class UIKitCommonRefreshFooter @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : UIKitLottieRefreshHeaderFooter(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "CommonRefreshFooter"
    }

    private var stopAnimation: Boolean = false
    private var noMoreData: Boolean = false

    private var stateTips: TextView? = null
    private var motionLayout: MotionLayout? = null
    private var pullToRefresh: ImageView? = null

    override val headerOrFooterLayout: Int
        get() = R.layout.uikit_layout_common_refresh_footer

    override val hasLottieAnimationView: Boolean
        get() = false

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        View.inflate(context, headerOrFooterLayout, this).run {
            stateTips = this.findViewById(R.id.state_tips)
            motionLayout = this.findViewById(R.id.motion_layout)
            pullToRefresh = this.findViewById(R.id.pull_to_refresh)
        }
    }

    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
        Log.d(TAG, "onMoving: 正在上拉中==$isDragging, 百分比 == $percent, 偏移 == $offset, 头部高度 == $height, 最大拖动高度 ==$maxDragHeight")
        super.onMoving(isDragging, percent, offset, height, maxDragHeight)
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        Log.d(TAG, "onFinish: 加载是否成功 == $success")
        stateTips?.text = if (success) "加载成功" else "加载失败"
        return 0
    }


    /**
     * 完整状态流转： None --->PullUpToLoad  --->PullUpCanceled --->ReleaseToLoad --->LoadReleased --->Loading --->LoadFinish --->None
     *                          上拉中           取消上拉加载       达到松手刷新临界        开始松手       加载刷新      加载结束
     */
    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        super.onStateChanged(refreshLayout, oldState, newState)
        Log.d(TAG, "onStateChanged: $oldState ---> $newState")

        when (newState) {
            RefreshState.PullUpToLoad -> {
                initFooterLayout()
            }

            RefreshState.ReleaseToLoad -> {
                motionLayout?.transitionToEnd()
            }

            RefreshState.LoadReleased -> {
                // 执行回弹动画不作任何操作
            }

            RefreshState.Loading -> {
                pullToRefresh?.setImageResource(R.drawable.uikit_loading)
                stateTips?.text = "正在加载"
                stopAnimation = false

                val valueAnimator = ValueAnimator.ofFloat(0f, 360f)
                valueAnimator.setDuration(500).interpolator = LinearInterpolator()
                valueAnimator.repeatCount = ValueAnimator.INFINITE
                valueAnimator.addUpdateListener {
                    pullToRefresh?.rotation = it.animatedValue as Float
                    if (stopAnimation) {
                        valueAnimator.cancel()
                    }
                }
                valueAnimator.start()
            }

            RefreshState.LoadFinish -> {
                stopAnimation = true
                pullToRefresh?.visibility = View.GONE
            }

            else -> {
            }
        }
    }

    private fun initFooterLayout() {
        if (!noMoreData) {
            pullToRefresh?.setImageResource(R.drawable.ic_upward_pull_24)
            pullToRefresh?.visibility = VISIBLE
            motionLayout?.visibility = VISIBLE
            motionLayout?.setTransition(R.id.pull_up_to_load_animate)
            motionLayout?.transitionToStart()
        } else {
            motionLayout?.transitionToState(R.id.no_more_data)
        }
    }

    /**
     * 仅在 noMoreData == true 时才有回调
     */
    override fun setNoMoreData(noMoreData: Boolean): Boolean {
        Log.d(TAG, "setNoMoreData: 没有更多数据 == $noMoreData")
        this.noMoreData = noMoreData
        initFooterLayout()
        return super.setNoMoreData(noMoreData)
    }
}