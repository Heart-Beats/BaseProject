package com.hl.baseproject.compose.utils

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * @author  张磊  on  2023/05/15 at 16:59
 * Email: 913305160@qq.com
 */


/**
 * 软键盘收起时，清除焦点
 */
@OptIn(ExperimentalLayoutApi::class)
fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
	var isFocused by remember { mutableStateOf(false) }
	var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }
	if (isFocused) {
		val imeIsVisible = WindowInsets.isImeVisible
		val focusManager = LocalFocusManager.current
		LaunchedEffect(imeIsVisible) {
			if (imeIsVisible) {
				keyboardAppearedSinceLastFocused = true
			} else if (keyboardAppearedSinceLastFocused) {
				focusManager.clearFocus()
			}
		}
	}
	onFocusEvent {
		if (isFocused != it.isFocused) {
			isFocused = it.isFocused
			if (isFocused) {
				keyboardAppearedSinceLastFocused = false
			}
		}
	}
}

/**
 * 水平滑动以动画效果移除元素
 *
 * @param  onDismissed   元素被移除时的通知，需要使用者去清除对应的数据
 */
@SuppressLint("ReturnFromAwaitPointerEventScope")
fun Modifier.swipeToDismiss(onDismissed: () -> Unit) = composed {
	val offsetX = remember { Animatable(0F) }

	val pointerInput = pointerInput(Unit) {
		// 用于计算滑动动画的稳定位置
		val decay = splineBasedDecay<Float>(this)
		// 包装在协程范围内，以使用暂停功能处理触摸事件和动画
		coroutineScope {
			while (true) {
				// 等待向下轻触事件, 根据触摸跟踪 pointerId
				val pointerId = awaitPointerEventScope {
					this.awaitFirstDown().id
				}

				if (offsetX.isRunning) {
					// 动画当前正在运行，停止拦截
					offsetX.stop()
				}

				//使用 velocityTracker 计算记录滑动手势的速度
				val velocityTracker = VelocityTracker()

				// 等待拖拽事件
				val isHorizontalDrag = awaitPointerEventScope {
					var horizontalDragOffset = offsetX.value
					var verticalOffset = 0F
					var isHorizontalDrag = false

					this.horizontalDrag(pointerId) { change ->
						val positionChange = change.positionChange()

						// 记录水平方向上总偏移
						horizontalDragOffset += positionChange.x
						// 记录水平方向上总偏移
						verticalOffset += positionChange.y

						// 记录拖动的速度，给定时间所处位置
						velocityTracker.addPosition(change.uptimeMillis, change.position)

						isHorizontalDrag = horizontalDragOffset.absoluteValue > verticalOffset.absoluteValue
								&& horizontalDragOffset.absoluteValue > 20

						if (isHorizontalDrag) {
							// 仅在水平拖拽时将偏移值设置为动画目标值, 这时就会更改元素位置
							launch { offsetX.snapTo(horizontalDragOffset) }

							// 消费触摸事件，不传递给外部
							change.consume()
						}
					}
					isHorizontalDrag
				}



				if (!isHorizontalDrag) {
					//  非水平拖拽时不作滑动删除，同时将元素置为默认位置
					if (offsetX.value != 0F) {
						offsetX.animateTo(targetValue = 0F)
					}
					continue
				}

				// 拖拽结束，计算水平拖拽速度
				val velocityX = velocityTracker.calculateVelocity().x
				// 计算元素在动画结束后最终的位置，这里是根据初始位置以及所处速度计算得出
				val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocityX)
				// 动画应在达到这些边界后立即结束
				offsetX.updateBounds(lowerBound = -size.width.toFloat(), upperBound = size.width.toFloat())
				launch {
					if (targetOffsetX.absoluteValue <= size.width) {
						// 速度不够，滑回默认位置
						offsetX.animateTo(targetValue = 0f, initialVelocity = velocityX)
					} else {
						// 足够的速度将元素滑到边缘。
						offsetX.animateDecay(velocityX, decay)
						// 通知元素可被删除
						onDismissed()
					}
				}
			}
		}
	}

	// 将水平偏移应用于元素， 不然 UI 看不出动画效果
	pointerInput.offset { IntOffset(offsetX.value.roundToInt(), 0) }
}