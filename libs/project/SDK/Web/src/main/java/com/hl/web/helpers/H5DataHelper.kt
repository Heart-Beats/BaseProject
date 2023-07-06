package com.hl.web.helpers

import androidx.core.content.edit
import com.hl.mmkvsharedpreferences.getApp
import com.hl.mmkvsharedpreferences.sharedPreferences

/**
 * @author  张磊  on  2022/06/13 at 16:45
 * Email: 913305160@qq.com
 */
object H5DataHelper {

	private var h5SpName = "${getApp().packageName}.h5.sharedPreferences"

	@JvmStatic
	fun putData(key: String, value: String?) {
		sharedPreferences(h5SpName).edit {
			this.putString(key, value)
		}
	}

	@JvmStatic
	fun getData(key: String): String {
		return sharedPreferences(h5SpName).getString(key, "") ?: ""
	}

	@JvmStatic
	fun clearData() {
		sharedPreferences(h5SpName).edit {
			this.clear()
		}
	}
}