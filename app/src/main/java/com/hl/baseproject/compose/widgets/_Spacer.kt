package com.hl.baseproject.compose.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @author  张磊  on  2023/06/02 at 17:30
 * Email: 913305160@qq.com
 */

/**
 * 指定宽高的间隔控件
 *
 * @param width  宽
 * @param height 高
 */
@Composable
fun Spacer(width: Dp = 0.dp, height: Dp = 0.dp) {
	Spacer(
		modifier = Modifier
			.width(width)
			.height(height)
	)
}