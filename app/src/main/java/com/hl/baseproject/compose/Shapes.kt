package com.hl.baseproject.compose

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * @author  张磊  on  2023/05/10 at 18:40
 * Email: 913305160@qq.com
 */

val MyShapes = Shapes(
	extraSmall = RoundedCornerShape(4.dp),
	small = RoundedCornerShape(8.dp),
	medium = RoundedCornerShape(16.dp),
	large = RoundedCornerShape(24.dp),
	extraLarge = RoundedCornerShape(32.dp)
)