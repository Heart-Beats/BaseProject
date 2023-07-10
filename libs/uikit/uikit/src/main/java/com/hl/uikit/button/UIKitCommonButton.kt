package com.hl.uikit.button

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.hl.uikit.R
import com.hl.uikit.utils.dpInt

/**
 * @Author  张磊  on  2020/10/09 at 17:03
 * Email: 913305160@qq.com
 */
class UIKitCommonButton : AppCompatButton {

	private companion object {
		const val SOLID_COLOR1_RADIUS4 = 0
		const val SOLID_COLOR1_RADIUS4_MIN_HIGH44 = 1
		const val SOLID_COLOR1_RADIUS0 = 2
		const val SOLID_COLOR_GRAY_RADIUS0 = 3
		const val STROKE_RADIUS4 = 4
		const val CAPSULE = 5
		const val CAPSULE_STROKE = 6
		const val CUSTOM = 7
	}

	private var buttonStyle: Int = SOLID_COLOR1_RADIUS4
	private var customDrawable: Drawable? = null

	constructor(context: Context) : this(context, null)

	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_commonButtonStyle)

	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitCommonButton, defStyleAttr, 0)
		buttonStyle = ta.getInt(R.styleable.UIKitCommonButton_uikit_buttonStyle, buttonStyle)
		customDrawable = ta.getDrawable(R.styleable.UIKitCommonButton_uikit_custom_drawable)
		ta.recycle()
		initStyle()
	}

	private fun initStyle() {
		when (buttonStyle) {
			SOLID_COLOR1_RADIUS4 -> {
				minHeight = 50.dpInt
				setBackgroundResource(R.drawable.uikit_main_button_radius4_selector)
			}

			SOLID_COLOR1_RADIUS4_MIN_HIGH44 -> {
				minHeight = 44.dpInt
				setBackgroundResource(R.drawable.uikit_main_button_radius4_selector)
			}

			SOLID_COLOR1_RADIUS0 -> {
				minHeight = 50.dpInt
				setBackgroundResource(R.drawable.uikit_main_button_radius0_selector)
			}

			SOLID_COLOR_GRAY_RADIUS0 -> {
				minHeight = 50.dpInt
				setBackgroundResource(R.drawable.uikit_main_button_gray_radius0_selector)
				setTextColor(ContextCompat.getColor(context, com.hl.uikitres.R.color.uikit_color_4))
			}

			STROKE_RADIUS4 -> {
				minHeight = 50.dpInt
				setBackgroundResource(R.drawable.uikit_stroke_button_radius4_selector)
				setTextColor(ContextCompat.getColor(context, com.hl.uikitres.R.color.uikit_color_1))
			}

			CAPSULE, CAPSULE_STROKE -> {
				textSize = 12f
				minHeight = 25.dpInt
				minWidth = 72.dpInt

				if (CAPSULE == buttonStyle) {
					setBackgroundResource(R.drawable.uikit_capsule_button_selector)
				} else {
					setBackgroundResource(R.drawable.uikit_stroke_capsule_button_selector)
					setTextColor(ContextCompat.getColor(context, com.hl.uikitres.R.color.uikit_color_1))
				}
			}
			CUSTOM -> {
				minHeight = 50.dpInt
				customDrawable?.run {
					background = this
				}
			}
		}

	}
}