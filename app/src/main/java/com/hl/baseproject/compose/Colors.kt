package com.hl.baseproject.compose

import androidx.compose.ui.graphics.Color
import java.security.SecureRandom

/**
 * @author  张磊  on  2023/04/21 at 15:41
 * Email: 913305160@qq.com
 */


// 在定义颜色时，我们要根据颜色值“照字面意义”命名颜色，而不要“从语义上”命名颜色， 这样一来，我们就可以定义多个主题

val md_theme_light_primary = Color(0xFF006C48)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFF73FBBC)
val md_theme_light_onPrimaryContainer = Color(0xFF002113)
val md_theme_light_secondary = Color(0xFF006C4F)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFF83F8CB)
val md_theme_light_onSecondaryContainer = Color(0xFF002116)
val md_theme_light_tertiary = Color(0xFF3C6472)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFC0E9FA)
val md_theme_light_onTertiaryContainer = Color(0xFF001F28)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFBFDF8)
val md_theme_light_onBackground = Color(0xFF191C1A)
val md_theme_light_surface = Color(0xFFFBFDF8)
val md_theme_light_onSurface = Color(0xFF191C1A)
val md_theme_light_surfaceVariant = Color(0xFFDCE5DD)
val md_theme_light_onSurfaceVariant = Color(0xFF404943)
val md_theme_light_outline = Color(0xFF707973)
val md_theme_light_inverseOnSurface = Color(0xFFEFF1ED)
val md_theme_light_inverseSurface = Color(0xFF2E312F)
val md_theme_light_inversePrimary = Color(0xFF53DEA2)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFF006C48)
val md_theme_light_outlineVariant = Color(0xFFC0C9C1)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFF53DEA2)
val md_theme_dark_onPrimary = Color(0xFF003824)
val md_theme_dark_primaryContainer = Color(0xFF005235)
val md_theme_dark_onPrimaryContainer = Color(0xFF73FBBC)
val md_theme_dark_secondary = Color(0xFF65DBB0)
val md_theme_dark_onSecondary = Color(0xFF003828)
val md_theme_dark_secondaryContainer = Color(0xFF00513B)
val md_theme_dark_onSecondaryContainer = Color(0xFF83F8CB)
val md_theme_dark_tertiary = Color(0xFFA4CDDD)
val md_theme_dark_onTertiary = Color(0xFF063542)
val md_theme_dark_tertiaryContainer = Color(0xFF234C5A)
val md_theme_dark_onTertiaryContainer = Color(0xFFC0E9FA)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF191C1A)
val md_theme_dark_onBackground = Color(0xFFE1E3DF)
val md_theme_dark_surface = Color(0xFF191C1A)
val md_theme_dark_onSurface = Color(0xFFE1E3DF)
val md_theme_dark_surfaceVariant = Color(0xFF404943)
val md_theme_dark_onSurfaceVariant = Color(0xFFC0C9C1)
val md_theme_dark_outline = Color(0xFF8A938C)
val md_theme_dark_inverseOnSurface = Color(0xFF191C1A)
val md_theme_dark_inverseSurface = Color(0xFFE1E3DF)
val md_theme_dark_inversePrimary = Color(0xFF006C48)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFF53DEA2)
val md_theme_dark_outlineVariant = Color(0xFF404943)
val md_theme_dark_scrim = Color(0xFF000000)


val seed = Color(0xFF1EB980)


fun randomColor(): Color {
	// SecureRandom 可产生真随机数
	val secureRandom = SecureRandom()

	val red = secureRandom.nextInt(256)
	val green = secureRandom.nextInt(256)
	val blue = secureRandom.nextInt(256)

	return Color(red, green, blue)
}