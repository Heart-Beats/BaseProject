package com.hl.arch.web.helpers

import androidx.core.content.edit
import com.blankj.utilcode.util.ActivityUtils
import com.hl.utils.sharedPreferences

/**
 * @author  张磊  on  2022/06/13 at 16:45
 * Email: 913305160@qq.com
 */
object H5DataHelper {

	@JvmStatic
	fun putData(key: String, value: String?) {
		ActivityUtils.getTopActivity().sharedPreferences().edit {
			this.putString(key, value)
		}
	}

	@JvmStatic
	fun getData(key: String): String {
		return ActivityUtils.getTopActivity().sharedPreferences().getString(key, "") ?: ""
	}
}