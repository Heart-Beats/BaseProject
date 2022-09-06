package com.hl.uikit

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.CollapsingToolbarLayout

/**
 * @author  张磊  on  2022/09/06 at 18:50
 * Email: 913305160@qq.com
 */
class UIKitCollapsingToolbarLayout : CollapsingToolbarLayout {

	private var mListener: OnScrimsListener? = null // 渐变监听

	/**
	 * 当前渐变状态,  true 正在渐变
	 */
	private var isCurrentScrimsShown: Boolean = false

	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	override fun setScrimsShown(shown: Boolean, animate: Boolean) {
		super.setScrimsShown(shown, animate)
		if (isCurrentScrimsShown != shown) {
			isCurrentScrimsShown = shown
			mListener?.onScrimsStateChange(shown)
		}
	}


	/**
	 *  添加渐变动画监听器
	 */
	fun addOnScrimsListener(onScrimsListener: OnScrimsListener) {
		this.mListener = onScrimsListener
	}

	/**
	 * CollapsingToolbarLayout 渐变监听器
	 */
	interface OnScrimsListener {

		/**
		 * 渐变状态变化
		 *
		 * @param shown         渐变开关,  true: 正在渐变
		 */
		fun onScrimsStateChange(shown: Boolean)
	}
}