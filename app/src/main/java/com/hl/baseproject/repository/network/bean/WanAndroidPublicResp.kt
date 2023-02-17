package com.hl.baseproject.repository.network.bean

import com.google.gson.annotations.SerializedName
import com.hl.arch.api.PublicResp

/**
 * @author  张磊  on  2023/02/17 at 17:45
 * Email: 913305160@qq.com
 */

class WanAndroidPublicResp<T> : PublicResp<T>() {

	@SerializedName("errorCode")
	override var code: String? = null

	@SerializedName("errorMsg")
	override var msg: String? = null
}