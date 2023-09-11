package com.hl.uikit.edittext

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.hjq.shape.view.ShapeEditText
import com.hl.uikit.R
import com.hl.uikit.utils.dp
import kotlin.math.min

/**
 * @author  张磊  on  2023/09/05 at 17:53
 * Email: 913305160@qq.com
 */

class UIKitShapeEditTextWithDelete : ShapeEditText {

	constructor(context: Context) : this(context, null)


	constructor(context: Context, attrs: AttributeSet?) : this(
		context,
		attrs,
		R.attr.uikit_shapeEditTextWithDeleteStyle
	)

	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		init(context, attrs)
	}

	private var deleteIconDrawable: Drawable? = null

	private var deleteIconMarginEnd = 13.33F.dp.toInt()


	private fun init(context: Context, attrs: AttributeSet?) {
		val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitShapeEditTextWithDelete)

		ta.also {
			deleteIconDrawable = it.getDrawable(R.styleable.UIKitShapeEditTextWithDelete_uikit_deleteIcon)
			deleteIconMarginEnd =
				it.getDimensionPixelSize(
					R.styleable.UIKitShapeEditTextWithDelete_uikit_deleteIcon_marginEnd,
					deleteIconMarginEnd
				)

		}.recycle()
	}

	val paint = Paint()

	private val deleteIconWidth: Int
		get() {
			val iconWidth = deleteIconDrawable?.intrinsicWidth ?: 0
			return min(min(width, height), iconWidth)
		}

	private val deleteIconHeight: Int
		get() {
			val iconHeight = deleteIconDrawable?.intrinsicHeight ?: 0
			return min(min(width, height), iconHeight)
		}

	init {
		paint.flags = Paint.ANTI_ALIAS_FLAG
	}


	override fun onDrawForeground(canvas: Canvas) {
		super.onDrawForeground(canvas)

		val isHasText = this.text?.toString()?.isNotEmpty() == true

		//保存当前画布内容
		canvas.save()

		// 当有输入内容且获得焦点时，绘制删除图标
		if (isHasText && hasFocus()) {
			drawDeleteIcon(canvas)
		} else {
			// 无任何输入内容时，恢复之前保存的画布内容， 即清空删除图标
			canvas.restore()
		}
	}

	private fun drawDeleteIcon(canvas: Canvas) {
		deleteIconDrawable?.run {
			val left = width - deleteIconWidth - deleteIconMarginEnd
			val top = (height - deleteIconHeight) / 2
			this.setBounds(left, top, left + deleteIconWidth, top + deleteIconHeight)
			this.draw(canvas)
		}
	}


	override fun onTouchEvent(event: MotionEvent): Boolean {
		when (event.action) {
			MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> {
				val x = event.x
				val y = event.y
				if (deleteIconDrawable?.bounds?.contains(x.toInt(), y.toInt()) == true) {
					text?.clear()
					// requestFocus()
					// showSoftInput(this, 0)
					return true
				}
			}
		}
		return super.onTouchEvent(event)
	}

	private fun showSoftInput(view: View, flags: Int) {
		val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		view.isFocusable = true
		view.isFocusableInTouchMode = true
		view.requestFocus()
		imm.showSoftInput(view, flags)
	}
}