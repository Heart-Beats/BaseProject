package com.hl.utils.views

import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.widget.TextView
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import com.hl.utils.BuildVersionUtil

/**
 * @author 张磊  on  2023/01/10 at 13:00
 * Email: 913305160@qq.com
 *
 * 提前获取 textview 行数
 */
internal object TextViewLinesUtil {

	/**
	 * TextView 未绘制时获取行数
	 *
	 * @param TextView       需要获取行数的 TextView
	 * @param textViewWidth  获取行数的 TextView 的宽度
	 */
	fun getTextViewLines(textView: TextView, @Px textViewWidth: Int): Int {
		val width = textViewWidth - textView.compoundPaddingLeft - textView.compoundPaddingRight
		val staticLayout = if (BuildVersionUtil.isOver6()) {
			getStaticLayout23(textView, width)
		} else {
			getStaticLayout(textView, width)
		}
		val lines = staticLayout.lineCount
		val maxLines = textView.maxLines

		return minOf(maxLines, lines)
	}

	/**
	 * sdk>=23
	 */
	@RequiresApi(api = Build.VERSION_CODES.M)
	private fun getStaticLayout23(textView: TextView, width: Int): StaticLayout {
		val builder = StaticLayout.Builder.obtain(textView.text, 0, textView.text.length, textView.paint, width)
			.setAlignment(Layout.Alignment.ALIGN_NORMAL)
			.setTextDirection(TextDirectionHeuristics.FIRSTSTRONG_LTR)
			.setLineSpacing(textView.lineSpacingExtra, textView.lineSpacingMultiplier)
			.setIncludePad(textView.includeFontPadding)
			.setBreakStrategy(textView.breakStrategy)
			.setHyphenationFrequency(textView.hyphenationFrequency)
			.setMaxLines(if (textView.maxLines == -1) Int.MAX_VALUE else textView.maxLines)

		if (BuildVersionUtil.isOver8()) {
			builder.setJustificationMode(textView.justificationMode)
		}

		if (textView.ellipsize != null && textView.keyListener == null) {
			builder.setEllipsize(textView.ellipsize)
				.setEllipsizedWidth(width)
		}
		return builder.build()
	}

	/**
	 * sdk<23
	 */
	private fun getStaticLayout(textView: TextView, width: Int): StaticLayout {
		return StaticLayout(
			textView.text, 0, textView.text.length,
			textView.paint, width,
			Layout.Alignment.ALIGN_NORMAL,
			textView.lineSpacingMultiplier,
			textView.lineSpacingExtra,
			textView.includeFontPadding,
			textView.ellipsize,
			width
		)
	}
}