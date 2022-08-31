package com.hl.utils.location

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings

/**
 * @author  张磊  on  2022/08/30 at 15:04
 * Email: 913305160@qq.com
 */
object LocationUtil {

	/**
	 *  是否打开 gps 开关
	 */
	fun isGpsEnable(context: Context): Boolean {
		val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		val gpsLocationEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
		val networkLocationEnable = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
		return gpsLocationEnable && networkLocationEnable
	}

	/**
	 * 前往打开 GPS 开关页面
	 */
	fun gotoLocationSourceSettingPage(context: Context) {
		val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
		context.startActivity(intent)
	}
}