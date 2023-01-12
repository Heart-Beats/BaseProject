package com.hl.uikit

import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.uikit_view_number_step.view.*


/**
 * @author chibb
 * @description:
 * @date :2020/8/21 16:57
 */


fun addDoubleListener(
    opView: UIKitNumberStepView,
    min: Double,
    max: Double,
    defValue: String?
) {

    opView.text = defValue ?: "0.00"
    opView.etInput.addTextChangedListener(doubleTextChangeWatch(min, max, after = {
        val newValue = it.toString().toDoubleOrNull() ?: 0.00
        opView.tvAddOperator.isEnabled = newValue < max
        opView.tvDelOperator.isEnabled = newValue > min
    }))
    opView.etInput.setOnFocusChangeListener { _, focus ->
        if (!focus) {
            var valueTxt = opView.etInput.text.toString().let {
                if (it == "") {
                    "0"
                } else {
                    it
                }
            }
            var value = valueTxt.toDouble()
            if (value < min) {
                opView.etInput.setText(yuan2formatYuan(value))
            }

        }

    }

    opView.setOnOperatorClickListener { type ->
        val oldValue = opView.text.toString().toDoubleOrNull() ?: 0.00
        var newValue = oldValue
        when (type) {
            UIKitNumberStepView.TYPE_ADD -> {
                if (oldValue < max) {
                    newValue = oldValue + 0.01
                    opView.text = yuan2formatYuan(newValue)
                }
            }
            UIKitNumberStepView.TYPE_DELETE -> {
                if (oldValue > min) {
                    newValue = oldValue - 0.01
                    opView.text = yuan2formatYuan(newValue)
                }
            }
            else -> {
            }
        }

        opView.tvAddOperator.isEnabled = newValue < max
        opView.tvDelOperator.isEnabled = newValue > min
    }

}

fun doubleTextChangeWatch(
    min: Double? = null,
    max: Double? = null,
    autoMax: Boolean? = true,
    autoMin: Boolean? = true,
    after: (editable: Editable?) -> Unit = {},
    inRange: (inRange: Boolean) -> Unit = {}
): TextWatcher {
    return object : TextWatcher {
        var stopWatch = false
        override fun afterTextChanged(editable: Editable?) {
            if (!stopWatch) {
                stopWatch = true
                val text = editable?.toString() ?: ""
                val dotIndex = text.lastIndexOf('.')
                val fNum = if (dotIndex < 0) {
                    0
                } else {
                    text.length - dotIndex - 1
                }
                if (fNum > 2) {
                    editable?.delete(dotIndex + 3, dotIndex + 4)
                } else {
                    val amount = text.toDoubleOrNull() ?: 0.0
                    val maxAmount = max ?: 0.0
                    val minAmount = min ?: 0.0
                    when {
                        autoMax == true && maxAmount > 0 && amount > maxAmount -> {
                            val yuan = yuan2formatYuan(maxAmount)
                            editable?.clear()
                            editable?.append(yuan)
                        }
                        autoMin == true && fNum == 2 && minAmount > 0 && amount < minAmount -> {
                            val yuan = yuan2formatYuan(minAmount)
                            editable?.clear()
                            editable?.append(yuan)
                        }
                        min != null && max != null -> {
                            inRange(amount in min..max)
                        }
                        min != null -> {
                            inRange(amount >= min)
                        }
                        max != null -> {
                            inRange(amount <= max)
                        }
                    }


                }
                stopWatch = false
                after(editable)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}

fun integerTextChangeWatch(
    min: Int? = null,
    max: Int? = null,
    autoMax: Boolean? = true,
    autoMin: Boolean? = true,
    after: (editable: Editable?) -> Unit = {}
): TextWatcher {
    return object : TextWatcher {
        var stopWatch = false
        override fun afterTextChanged(editable: Editable?) {
            if (!stopWatch) {
                stopWatch = true
                val maxValue=max?:0
                val minValue=min?:0
                var value=editable.toString().toIntOrNull()?:0
                if(value > maxValue){
                    value=maxValue
                }

                if(value<minValue ){
                    value=minValue
                }
                editable?.clear()
                editable?.append(value.toString())
                stopWatch = false
                after(editable)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}

fun yuan2formatYuan(yuan: Double): String {
    return String.format("%.2f", yuan)
}

fun <T> MutableList<T>.addMuch(vararg element: T): MutableList<T> {
    element.forEach {
        this.add(it)
    }
    return this
}
