package com.hl.ui.base

import android.view.LayoutInflater
import android.view.View

/**
 * @author  张磊  on  2023/02/10 at 13:33
 * Email: 913305160@qq.com
 */
interface IPageInflate {
	/**
	 * 获取页面填充的 View
	 */
	fun getPageInflateView(layoutInflater: LayoutInflater): View
}