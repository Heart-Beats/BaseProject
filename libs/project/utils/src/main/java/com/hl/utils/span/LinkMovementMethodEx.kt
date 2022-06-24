package com.hl.utils.span

import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.MotionEvent
import android.widget.TextView

/**
 * LinkMovementMethod 实现子类， 可实现 ClickableSpan 的自定义点击事件处理，并解决与长按事件的冲突
 *
 * @author 张磊  on  2022/06/20 at 14:38
 * Email: 913305160@qq.com
 */
class LinkMovementMethodEx(private val listener: OnLinkClick) : LinkMovementMethod() {

	companion object {
		/**
		 * 处理 ClickableSpan 与 长按事件冲突的 tag, 长按时设置 tag 值即可
		 */
		@JvmStatic
		var LONG_CLICK = LinkMovementMethodEx::class.java.hashCode()
	}

	override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
		val action = event.action
		if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
			var x = event.x.toInt()
			var y = event.y.toInt()
			x -= widget.totalPaddingLeft
			y -= widget.totalPaddingTop
			x += widget.scrollX
			y += widget.scrollY
			val layout = widget.layout
			val line = layout.getLineForVertical(y)
			val off = layout.getOffsetForHorizontal(line, x.toFloat())
			val links = buffer.getSpans(off, off, ClickableSpan::class.java)
			if (links.isNotEmpty()) {
				if (action == MotionEvent.ACTION_UP) {

					// 处理长按与 ClickableSpan 的事件冲突，当长按时不会触发 ClickableSpan 的点击事件
					if (widget.getTag(LONG_CLICK) != null) {
						widget.setTag(LONG_CLICK, null)
						return false
					}
					if (links[0] is URLSpan) {
						val url = links[0] as URLSpan
						if (listener(url.url)) {
							return true
						} else {
							links[0].onClick(widget)
						}
					}
				} else if (action == MotionEvent.ACTION_DOWN) {
					Selection.setSelection(
						buffer,
						buffer.getSpanStart(links[0]),
						buffer.getSpanEnd(links[0])
					)
				}
				return true
			} else {
				Selection.removeSelection(buffer)
			}
		}
		return super.onTouchEvent(widget, buffer, event)
	}
}

/**
 *
 *
 * @param mURL     点击链接的地址
 * @return  true  表示要自己处理  false 使用系统默认
 */
typealias OnLinkClick = (mURL: String) -> Boolean