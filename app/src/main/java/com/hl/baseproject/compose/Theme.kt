package com.hl.baseproject.compose

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

/**
 * @author  张磊  on  2023/04/21 at 15:43
 * Email: 913305160@qq.com
 */

private val LightColorScheme = lightColorScheme(
	primary = PrimaryColor,
	secondary = SecondaryColor,
)

private val DarkColorScheme = darkColorScheme(
	primary = PrimaryColor,
	secondary = SecondaryColor,
)

/**
 * 基于 Compose 的 app 主题设置
 *
 * @param  darkTheme      是否为深色模式
 * @param  dynamicColor   是否需要动态颜色
 */
@Composable
fun AppComposeTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	dynamicColor: Boolean = true,
	content: @Composable () -> Unit
) {

	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			// Android 12 以上可以使用动态颜色
			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}

		darkTheme -> DarkColorScheme
		else -> LightColorScheme
	}

	val view = LocalView.current
	if (!view.isInEditMode) {
		SideEffect {
			val window = (view.context as Activity).window
			window.statusBarColor = colorScheme.primary.toArgb()
			WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
		}
	}

	val typography = Typography(
		titleLarge = TextStyle(
			fontWeight = FontWeight.W100,
			fontSize = 96.sp
		),
		bodyLarge = TextStyle(
			fontWeight = FontWeight.W600,
			fontSize = 14.sp
		)
	)

	MaterialTheme(colorScheme = colorScheme, typography = typography, content = content)
}