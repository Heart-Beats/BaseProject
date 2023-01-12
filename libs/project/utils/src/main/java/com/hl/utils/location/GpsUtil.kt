package com.hl.utils.location

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import com.blankj.utilcode.util.ActivityUtils
import com.hl.utils.BaseUtil
import com.hl.utils.activityResult.ActivityResultHelper
import com.hl.utils.activityResult.OnActivityResult
import com.hl.utils.showPop

/**
 * @author  张磊  on  2023/01/12 at 11:48
 * Email: 913305160@qq.com
 */
object GpsUtil {

	/**
	 * 检测 GPS 是否可用， 不可用时会引导用户打开
	 */
	fun checkGpsEnable(activityResultHelper: ActivityResultHelper, onResult: (isGpsEnable: Boolean) -> Unit) {
		if (isGpsEnable()) {
			onResult(true)
			return
		}

		RequestOpenGPSPop(ActivityUtils.getTopActivity()).apply {
			this.onSureAction = {
				gotoLocationSourceSettingPage(activityResultHelper, onResult)
			}
		}.showPop {
			this.dismissOnTouchOutside(false)
			this.dismissOnBackPressed(false)
		}
	}

	/**
	 *  是否打开 gps 开关
	 */
	fun isGpsEnable(): Boolean {
		val manager = BaseUtil.app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		val gpsLocationEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
		val networkLocationEnable = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
		return gpsLocationEnable && networkLocationEnable
	}

	/**
	 * 前往打开 GPS 开关页面
	 */
	fun gotoLocationSourceSettingPage(
		activityResultHelper: ActivityResultHelper,
		onResult: (isGpsEnable: Boolean) -> Unit
	) {

		val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
		activityResultHelper.launchIntent(intent, callback = object : OnActivityResult {
			override fun onResultOk(data: Intent?) {}

			override fun onResultCanceled(data: Intent?) {
				// 访问位置信息只会回调这里来
				onResult(isGpsEnable())
			}
		})
	}
}