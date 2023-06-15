package com.hl.arch.base

import android.graphics.Bitmap
import com.hl.arch.R
import com.hl.utils.OnPaletteColorParse
import com.hl.utils.PaletteUtil
import com.hl.utils.getColorByRes

/**
 * @author  张磊  on  2023/06/15 at 17:38
 * Email: 913305160@qq.com
 */
abstract class BaseActivity:com.hl.ui.base.BaseActivity() {

	/**
	 * 该方法可重写更改状态栏颜色, 默认与标题栏保持同色
	 */
	override fun getStatusBarColor(): Int {
		return getColorByRes(R.color.colorTitlePrimary)
	}


	/**
	 * 从 bitmap 中分析修改状态栏对应的颜色以及字体颜色
	 *
	 * @param bitmap              需要分析的 bitmap
	 * @param onPaletteColorParse 分析结果的回调
	 */
	protected fun changeStatusBarStyleFromBitmap(bitmap: Bitmap, onPaletteColorParse: OnPaletteColorParse? = null) {
		PaletteUtil.getColorFromBitmap(bitmap) { rgb, bodyTextColor, titleTextColor, isLight ->
			onPaletteColorParse?.invoke(rgb, bodyTextColor, titleTextColor, isLight)
			immersionBar?.apply {
				statusBarColorInt(rgb)
				statusBarDarkFont(isLight)
			}?.init()
		}
	}
}