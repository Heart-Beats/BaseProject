package com.hl.web.bean

import com.hl.json.GsonUtil


/**
 * @author  张磊  on  2022/06/13 at 16:32
 * Email: 913305160@qq.com
 */

data class H5ReturnBean<T>(
	var status: String = "",
	var msg: String = "",
	var data: T
) {
	fun toJsonString(): String {
		return GsonUtil.toJson(this)
	}
}

object H5Return {

	@JvmStatic
	fun success(): String {
		return success("")
	}

	@JvmStatic
	fun fail(): String {
		return fail("")
	}

	@JvmStatic
	fun <T> success(data: T): String {
		return H5ReturnBean("success", "操作成功", data).toJsonString()
	}

	@JvmStatic
	fun <T> fail(data: T): String {
		return H5ReturnBean("fail", "操作失败", data).toJsonString()
	}
}
