package com.hl.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import java.lang.reflect.Method
import java.util.*

/**
 * @Author  张磊  on  2020/09/28 at 9:58
 * Email: 913305160@qq.com
 */
object DeviceInfoUtil {
	/**
	 * 获取设备宽度（px）
	 *
	 */
	fun getDeviceWidth(context: Context): Int {
		return context.resources.displayMetrics.widthPixels
	}

	/**
	 * 获取设备高度（px）
	 */
	fun getDeviceHeight(context: Context): Int {
		return context.resources.displayMetrics.heightPixels
	}

	/**
	 * 获取设备的唯一标识， 需要 “android.permission.READ_Phone_STATE”权限
	 */
	@SuppressLint("HardwareIds")
	@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
	@RequiresApi(Build.VERSION_CODES.O)
	fun getIMEIDeviceId(context: Context): String {
		val deviceId: String

		deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
		} else {
			val mTelephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
			if (mTelephony.deviceId != null) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					mTelephony.imei
				} else {
					mTelephony.deviceId
				}
			} else {
				Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
			}
		}
		return deviceId
	}

	/**
	 * 获取厂商名
	 */
	fun getDeviceManufacturer(): String {
		return Build.MANUFACTURER
	}

	/**
	 * 获取产品名
	 */
	fun getDeviceProduct(): String {
		return Build.PRODUCT
	}

	/**
	 * 获取手机品牌
	 */
	fun getDeviceBrand(): String {
		return Build.BRAND
	}

	/**
	 * 获取手机型号
	 */
	fun getDeviceModel(): String {
		return Build.MODEL
	}

	/**
	 * 获取手机主板名
	 */
	fun getDeviceBoard(): String {
		return Build.BOARD
	}

	/**
	 * 设备名
	 */
	fun getDeviceName(): String {
		return Build.DEVICE
	}

	/**
	 *
	 *
	 * FingerPrint 信息
	 */
	fun getDeviceFingerPrint(): String {
		return Build.FINGERPRINT
	}

	/**
	 * 硬件名
	 *
	 */
	fun getDeviceHardware(): String {
		return Build.HARDWARE
	}


	/**
	 *
	 * 显示ID
	 */
	fun getDeviceDisplay(): String {
		return Build.DISPLAY
	}

	/**
	 * ID
	 *
	 */
	fun getDeviceId(): String {
		return Build.ID
	}

	/**
	 * 获取手机用户名
	 *
	 */
	fun getDeviceUser(): String {
		return Build.USER
	}


	/**
	 * 获取手机Android 系统SDK
	 *
	 * @return
	 */
	fun getDeviceSDK(): Int {
		return Build.VERSION.SDK_INT
	}

	/**
	 * 获取手机Android 版本
	 *
	 * @return
	 */
	fun getDeviceAndroidVersion(): String {
		return Build.VERSION.RELEASE
	}

	/**
	 * 获取当前手机系统语言。
	 */
	fun getDeviceDefaultLanguage(): String {
		return Locale.getDefault().language
	}

	/**
	 * 获取当前系统上的语言列表(Locale列表)
	 */
	fun getDeviceSupportLanguage(): List<String> {
		return Locale.getAvailableLocales().map { it.displayLanguage }
	}

	/**
	 * 获取EMUI版本号
	 */
	fun getEMUI(): String {
		// //[ro.build.version.emui]: [EmotionUI_10.1.0]
		var buildVersion = ""
		try {
			val classType = Class.forName("android.os.SystemProperties")
			val getMethod: Method = classType.getDeclaredMethod("get", String::class.java)
			buildVersion = getMethod.invoke(classType, "ro.build.version.emui") as String
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return buildVersion.split("_").last().split(".").first()
	}

	/**
	 * 获取当前手机的一些相关信息
	 */
	@RequiresApi(Build.VERSION_CODES.O)
	@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
	fun getDeviceAllInfo(context: Context): String {
		return """
				1. IMEI或者设备ID:
						${getIMEIDeviceId(context)}
				
				2. 设备宽度:
						${getDeviceWidth(context)}
				
				3. 设备高度:
						${getDeviceHeight(context)}
				
				4. 是否联网:
						${NetworkUtil.isConnected(context)}
				
				5. 网络类型:
						${NetworkUtil.getNetworkInfo(context)}
				
				6. 系统默认语言:
						${getDeviceDefaultLanguage()}
				
				7. 手机型号:
						${getDeviceModel()}
				
				8. 生产厂商:
						${getDeviceManufacturer()}
				
				9. 手机Fingerprint标识:
						${getDeviceFingerPrint()}
						
				10. 用户名:
						${getDeviceUser()}
				
				11. 设备名:
						${getDeviceName()}	

				12. 手机品牌: 
						${getDeviceBrand()}

				13. Android 版本:
						${getDeviceAndroidVersion()}
						
				14. Android SDK版本:
						${getDeviceSDK()}

				15. ID:
						${getDeviceId()}
				
				16. 显示ID:
						${getDeviceDisplay()}
				
				17. 硬件名:
						${getDeviceHardware()}
				
				18. 产品名:
						${getDeviceProduct()}

				19. 主板名:
						${getDeviceBoard()}
				
				20. 语言支持:
						${getDeviceSupportLanguage()}
						"""
	}
}

enum class DeviceInfo(val manufacturer: String, val brandName: String) {
	HUA_WEI("huawei", "华为"),
	VIVO("vivo", "VIVO"),
	OPPO("oppo", "OPPO"),
	XIAO_MI("xiaomi", "小米"),
	SAMSUNG("samsung", "三星")
}

fun getDeviceByManufacturer(manufacturer: String): DeviceInfo? {
	return DeviceInfo.values().find {
		it.manufacturer == manufacturer.toLowerCase()
	}
}