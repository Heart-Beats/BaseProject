package com.hl.utils.views

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.CompoundButton
import android.widget.EditText

/**
 * @author  张磊  on  2021/06/07 at 9:38
 * Email: 913305160@qq.com
 */
class TextChangedListener(
	private val beforeTextChanged: (
		text: CharSequence?,
		start: Int,
		count: Int,
		after: Int
	) -> Unit = { _, _, _, _ -> },
	private val onTextChanged: (
		text: CharSequence?,
		start: Int,
		before: Int,
		count: Int
	) -> Unit = { _, _, _, _ -> },
	private val afterTextChanged: (editable: Editable?, textWatcher: TextWatcher) -> Unit = { _, _ -> }
) : TextWatcher {
	override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
		beforeTextChanged.invoke(text, start, count, after)
	}

	override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
		onTextChanged.invoke(text, start, before, count)
	}

	override fun afterTextChanged(s: Editable?) {
		afterTextChanged.invoke(s, this)
	}
}

fun textChangeWatcher(
	before: TextWatcher.(text: CharSequence?, start: Int, count: Int, after: Int) -> Unit = { _, _, _, _ -> },
	on: TextWatcher.(text: CharSequence?, start: Int, count: Int) -> Unit = { _, _, _ -> },
	after: TextWatcher.(editable: Editable?) -> Unit = {}
): TextWatcher {
	return object : TextWatcher {
		override fun afterTextChanged(s: Editable?) {
			after(s)
		}

		override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
			before(s, start, count, after)
		}

		override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
			on(s, start, count)
		}
	}
}


fun cardTextChangeWatcher(afterListener: TextWatcher.(Editable?) -> Unit = {}): TextWatcher {
	return object : TextWatcher {
		var stopWatch = false
		var selectIndex = 0
		override fun afterTextChanged(editable: Editable?) {
			if (!stopWatch) {
				stopWatch = true
				formatBankCardText(editable)
				val length = editable?.length ?: 0
				if (selectIndex > length) {
					selectIndex = length
				}
				Selection.setSelection(editable, selectIndex)
				stopWatch = false
				afterListener(editable)
			}
		}

		override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
		}

		override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
			if (!stopWatch) {
				val end = start + count
				val temp = s?.subSequence(0, end)
				selectIndex = formatBankCardText(temp ?: "").length
			}
		}
	}
}

private fun formatBankCardText(editable: Editable?) {
	val text = editable?.toString() ?: ""
	if (text.isEmpty()) {
		return
	}
	editable?.clear()
	editable?.append(formatBankCardText(text))
}

fun formatBankCardText(text: CharSequence): CharSequence {
	val value = text.filter { it != ' ' }
	val length = value.length
	if (length <= 0) {
		return ""
	}
	var numCnt = 0
	return buildString {
		for (index in 0 until length) {
			if (numCnt == 4) {
				append(' ')
				numCnt = 0
			}
			value.getOrNull(index)?.let {
				append(it)
				numCnt++
			}
		}
	}
}

/**
 * 是否显示或隐藏密码
 */
fun EditText.displayOrHidePassword(isDisplay: Boolean) {
	if (isDisplay) {
		this.transformationMethod = HideReturnsTransformationMethod.getInstance()
	} else {
		this.transformationMethod = PasswordTransformationMethod.getInstance()
	}
}

/**
 * EditText 与 CompoundButton 关联切换显示 明文/密码
 *
 * 常用的 CompoundButton 子类： [android.widget.ToggleButton]、[android.widget.CheckBox]、
 *   [android.widget.RadioButton]、[android.widget.Switch]
 */
fun EditText.attachDisplayPWCompoundButton(compoundButton: CompoundButton) {
	compoundButton.setOnCheckedChangeListener { _, isChecked ->
		val start = this.selectionStart
		val end = this.selectionEnd
		this.displayOrHidePassword(isChecked)
		this.setSelection(start, end)
	}
}

fun EditText.text2Int(): Int? {
	return this.text.toString().trim().toIntOrNull()
}

fun EditText.text2String(): String {
	return this.text.toString().trim()
}