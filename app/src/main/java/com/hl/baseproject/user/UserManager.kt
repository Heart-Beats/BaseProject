package com.hl.baseproject.user

import android.text.TextUtils
import androidx.core.content.edit
import com.hl.mmkvsharedpreferences.getObject
import com.hl.mmkvsharedpreferences.putObject
import com.hl.mmkvsharedpreferences.sharedPreferences


/**
 * @author  张磊  on  2022/01/05 at 11:55
 * Email: 913305160@qq.com
 */
object UserManager {

	private const val SP_USER_KEY = "SP_USER_KEY"


	fun setUser(userInfo: UserInfo?) {
		val localUserInfo = getUser()
		if (!TextUtils.isEmpty(localUserInfo.token)) {
			if (null != userInfo && TextUtils.isEmpty(userInfo.token)) {
				// 仅仅只有接口返回的用户信息中不带 token， 才使用本地存储的 token  进行覆盖
				userInfo.token = localUserInfo.token
			}
		}

		sharedPreferences().edit {
			putObject(SP_USER_KEY, userInfo ?: "")
		}
	}

	fun getUser(): UserInfo {
		return sharedPreferences().getObject<UserInfo>(SP_USER_KEY) ?: UserInfo()
	}
}