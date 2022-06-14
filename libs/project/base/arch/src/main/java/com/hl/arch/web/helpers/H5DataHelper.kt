package com.hl.arch.web.helpers

import androidx.core.content.edit
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.hl.utils.sharedPreferences

/**
 * @author  张磊  on  2022/06/13 at 16:45
 * Email: 913305160@qq.com
 */
object H5DataHelper {

	private var h5SpName = "${AppUtils.getAppInfo()?.packageName}.h5.sharedPreferences"

	@JvmStatic
	fun putData(key: String, value: String?) {
		ActivityUtils.getTopActivity().sharedPreferences(h5SpName).edit {
			this.putString(key, value)
		}
	}

	@JvmStatic
	fun getData(key: String): String {
		return ActivityUtils.getTopActivity().sharedPreferences(h5SpName).getString(key, "") ?: ""
	}

	@JvmStatic
	fun clearData() {
		ActivityUtils.getTopActivity().sharedPreferences(h5SpName).edit {
			this.clear()
		}
	}
}