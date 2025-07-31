package com.hl.utils.span

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ActivityUtils

/**
 * @author  张磊  on  2022/06/16 at 17:42
 * Email: 913305160@qq.com
 */


/**
 * span相关
 * textView.sizeSpan(str, 0..2, scale = .7f) //改变scale可以控制放大或缩小，scale默认是1.5
 * textView.colorSpan(str,2..6)
 * textView.backgroundColorSpan(str,2..6)
 * textView.strikeThrougthSpan(str,2..6)
 * textView.clickSpan(str = str, range = 2..6, color = Color.BLUE, clickAction = {
 *  toast("哈哈我被点击了".toColorSpan(0..2))
 *  })
 * textView.styleSpan(str, range) //加粗，斜体等效果
 *

实际项目中append系列方法会用的更多，用法按如下
tv.text = "演示一下appendXX方法的用法"
tv.appendSizeSpan("变大变大")
.appendColorSpan("我要变色", color = Color.parseColor("#f0aafc"))
.appendBackgroundColorSpan("我是有底色的", color = Color.parseColor("#cacee0"))
.appendStrikeThrougthSpan("添加删除线哦哦哦哦")
.appendClickSpan("来点我一下试试啊", isUnderlineText = true, clickAction = {
toast("哎呀，您点到我了呢，嘿嘿")
} )
.appendStyleSpan("我是粗体的")

 */


/**
 * 为指定的字符串追加 span
 * @param [spanString]  span 字符串
 * @return [SpannableStringBuilder]
 */
fun CharSequence.appendSpan(spanString: Spanned): SpannableStringBuilder {
	return SpannableStringBuilder().apply {
		append(this@appendSpan)
		append(spanString)
	}
}

/**
 * 支持 Spanned + Spanned 拼接
 */
operator fun Spanned.plus(spanString: Spanned): SpannableStringBuilder {
	return this.appendSpan(spanString)
}


/******************************** SpannableString 实现相关文本样式扩展 ********************************/


/**
 * 将一段文字中指定range的文字绝对改变大小
 * @param range 要改变大小的文字的范围
 * @param size  文字大小的值， 默认 14
 * @param isSp  单位是否为 sp，默认 true
 */
fun CharSequence.toSizeSpan(range: IntRange = 0..this.length, size: Int = 14, isSp: Boolean = true): SpannableString {
	return SpannableString(this).apply {
		setSpan(
			AbsoluteSizeSpan(size, isSp),
			range.first,
			range.last,
			Spannable.SPAN_INCLUSIVE_EXCLUSIVE
		)
	}
}

/**
 * 将一段文字中指定range的文字相对改变大小
 * @param range 要改变大小的文字的范围
 * @param scale 缩放值，大于1，则比其他文字大；小于1，则比其他文字小；默认是1.5
 */
fun CharSequence.toScaleSpan(range: IntRange = 0..this.length, scale: Float = 1.5f): SpannableString {
	return SpannableString(this).apply {
		setSpan(
			RelativeSizeSpan(scale),
			range.first,
			range.last,
			Spannable.SPAN_INCLUSIVE_EXCLUSIVE
		)
	}
}

/**
 * 将一段文字中指定range的文字改变前景色
 * @param range 要改变前景色的文字的范围
 * @param color 要改变的颜色，默认是红色
 */
fun CharSequence.toColorSpan(range: IntRange = 0..this.length, color: Int = Color.RED): SpannableString {
	return SpannableString(this).apply {
		setSpan(
			ForegroundColorSpan(color),
			range.first,
			range.last,
			Spannable.SPAN_INCLUSIVE_EXCLUSIVE
		)
	}
}

/**
 * 将一段文字中指定range的文字改变背景色
 * @param range 要改变背景色的文字的范围
 * @param color 要改变的颜色，默认是红色
 */
fun CharSequence.toBackgroundColorSpan(range: IntRange = 0..this.length, color: Int = Color.RED): SpannableString {
	return SpannableString(this).apply {
		setSpan(
			BackgroundColorSpan(color),
			range.first,
			range.last,
			Spannable.SPAN_INCLUSIVE_EXCLUSIVE
		)
	}
}

/**
 * 将一段文字中指定range的文字添加删除线
 * @param range 要添加删除线的文字的范围
 */
fun CharSequence.toStrikeThroughSpan(range: IntRange = 0..this.length): SpannableString {
	return SpannableString(this).apply {
		setSpan(
			StrikethroughSpan(),
			range.first,
			range.last,
			Spannable.SPAN_INCLUSIVE_EXCLUSIVE
		)
	}
}

/**
 * 将一段文字中指定range的文字添加颜色和点击事件
 *
 * 注意： TextView 必须设置 movementMethod = LinkMovementMethod.getInstance() ， 否则点击事件不生效
 *
 * @param range 目标文字的范围
 */
fun CharSequence.toClickSpan(range: IntRange = 0..this.length, color: Int = Color.RED, isUnderlineText: Boolean = true, clickAction: (() -> Unit)?): SpannableString {
	return SpannableString(this).apply {
		val clickableSpan = object : ClickableSpan() {
			override fun onClick(widget: View) {
				clickAction?.invoke()
			}

			override fun updateDrawState(ds: TextPaint) {
				ds.color = color
				ds.isUnderlineText = isUnderlineText
			}
		}
		setSpan(clickableSpan, range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
	}
}

/**
 * 将一段文字中指定range的文字添加style效果
 * @param range 要添加删除线的文字的范围
 */
fun CharSequence.toStyleSpan(style: Int = Typeface.BOLD, range: IntRange = 0..this.length): SpannableString {
	return SpannableString(this).apply {
		setSpan(
			StyleSpan(style),
			range.first,
			range.last,
			Spannable.SPAN_INCLUSIVE_EXCLUSIVE
		)
	}
}

/**
 * 将一段文字中指定range的文字添加自定义效果
 * @param range 要添加删除线的文字的范围
 */
fun CharSequence.toCustomTypeFaceSpan(typeface: Typeface, range: IntRange = 0..this.length): SpannableString {
	return SpannableString(this).apply {
		setSpan(
			CustomTypefaceSpan(typeface),
			range.first,
			range.last,
			Spannable.SPAN_INCLUSIVE_EXCLUSIVE
		)
	}
}


/**
 * 将一段文字中指定range的文字添加自定义效果,可以设置对齐方式，可以设置margin
 * @param range
 */
fun CharSequence.toImageSpan(
	imageRes: Int,
	range: IntRange = 0..this.length,
	verticalAlignment: Int = 0,  //默认底部  4是垂直居中
	marginLeft: Int = 0,
	marginRight: Int = 0,
	width: Int = 0,
	height: Int = 0
): SpannableString {
	return SpannableString(this).apply {
		setSpan(
			MiddleIMarginImageSpan(
				ContextCompat.getDrawable(ActivityUtils.getTopActivity(), imageRes)?.apply {
					setBounds(
						0, 0,
						if (width == 0) intrinsicWidth else width,
						if (height == 0) intrinsicHeight else height
					)
				},
				verticalAlignment,
				marginLeft,
				marginRight
			),
			range.first,
			range.last,
			Spannable.SPAN_INCLUSIVE_EXCLUSIVE
		)
	}
}


/******************************** TextView的扩展 ,本质上还是调用上面的方法  ********************************/
fun TextView.sizeSpan(str: String = "", range: IntRange = 0..str.length, textSize: Int = 14, isSp: Boolean = true): TextView {
	text = (str.ifEmpty { text }).toSizeSpan(range, textSize, isSp)
	return this
}

fun TextView.appendSizeSpan(str: String = "", textSize: Int = 14, isSp: Boolean = true): TextView {
	append(str.toSizeSpan(0..str.length, textSize, isSp))
	return this
}

fun TextView.scaleSpan(str: String = "", range: IntRange = 0..str.length, scale: Float = 1.5f): TextView {
	text = (str.ifEmpty { text }).toScaleSpan(range, scale)
	return this
}

fun TextView.appendScaleSpan(str: String = "", scale: Float = 1.5f): TextView {
	append(str.toScaleSpan(0..str.length, scale))
	return this
}

fun TextView.colorSpan(str: String = "", range: IntRange = 0..str.length, color: Int = Color.RED): TextView {
	text = (str.ifEmpty { text }).toColorSpan(range, color)
	return this
}

fun TextView.appendColorSpan(str: String = "", color: Int = Color.RED): TextView {
	append(str.toColorSpan(0..str.length, color))
	return this
}

fun TextView.backgroundColorSpan(
	str: String = "",
	range: IntRange = 0..str.length,
	color: Int = Color.RED
): TextView {
	text = (str.ifEmpty { text }).toBackgroundColorSpan(range, color)
	return this
}

fun TextView.appendBackgroundColorSpan(str: String = "", color: Int = Color.RED): TextView {
	append(str.toBackgroundColorSpan(0..str.length, color))
	return this
}

fun TextView.strikeThrougthSpan(str: String = "", range: IntRange = 0..str.length): TextView {
	text = (str.ifEmpty { text }).toStrikeThroughSpan(range)
	return this
}

fun TextView.appendStrikeThrougthSpan(str: String = ""): TextView {
	append(str.toStrikeThroughSpan(0..str.length))
	return this
}

fun TextView.clickSpan(
	str: String = "", range: IntRange = 0..str.length,
	color: Int = Color.RED, isUnderlineText: Boolean = true, clickAction: (() -> Unit)?
): TextView {
	if (clickAction != null) {
		movementMethod = LinkMovementMethod.getInstance()
		highlightColor = Color.TRANSPARENT  // remove click bg color
	}

	text = (str.ifEmpty { text }).toClickSpan(range, color, isUnderlineText, clickAction)
	return this
}

fun TextView.appendClickSpan(
	str: String = "", color: Int = Color.RED,
	isUnderlineText: Boolean = true, clickAction: (() -> Unit)?
): TextView {
	if (clickAction != null) {
		movementMethod = LinkMovementMethod.getInstance()
		highlightColor = Color.TRANSPARENT  // remove click bg color
	}

	append(str.toClickSpan(0..str.length, color, isUnderlineText, clickAction))
	return this
}

fun TextView.styleSpan(str: String = "", range: IntRange = 0..str.length, style: Int = Typeface.BOLD): TextView {
	text = (str.ifEmpty { text }).toStyleSpan(style = style, range = range)
	return this
}

fun TextView.appendStyleSpan(str: String = "", style: Int = Typeface.BOLD): TextView {
	append(str.toStyleSpan(style = style, range = 0..str.length))
	return this
}

fun TextView.customTypeFaceSpan(str: String = "", range: IntRange = 0..str.length, typeface: Typeface): TextView {
	text = (str.ifEmpty { text }).toCustomTypeFaceSpan(typeface, range = range)
	return this
}

fun TextView.appendCustomTypeFaceSpan(str: String = "", typeface: Typeface): TextView {
	append(str.toCustomTypeFaceSpan(typeface, range = 0..str.length))
	return this
}

fun TextView.imageSpan(
	imageRes: Int,
	verticalAlignment: Int = 0,  //默认底部
	str: String = "",
	range: IntRange = 0..str.length,
	marginLeft: Int = 0,
	marginRight: Int = 0,
	width: Int = 0,
	height: Int = 0
): TextView {
	text = (str.ifEmpty { text }).toImageSpan(
		imageRes,
		range = range,
		verticalAlignment = verticalAlignment,
		marginLeft = marginLeft,
		marginRight = marginRight,
		width = width,
		height = height
	)
	return this
}


fun TextView.appendImageSpan(
	imageRes: Int,
	verticalAlignment: Int = 0,
	str: String = "1",
	marginLeft: Int = 0,
	marginRight: Int = 0,
	width: Int = 0,
	height: Int = 0
): TextView {
	append(
		str.toImageSpan(
			imageRes,
			range = 0..str.length,
			verticalAlignment = verticalAlignment,
			marginLeft = marginLeft,
			marginRight = marginRight,
			width = width,
			height = height
		)
	)
	return this
}
