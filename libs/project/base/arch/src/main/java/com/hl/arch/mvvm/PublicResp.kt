package com.hl.arch.mvvm

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * @author  张磊  on  2021/11/02 at 19:41
 * Email: 913305160@qq.com
 *
 * 接口返回数据公共类，因为字段名称需保持一致，因此不可混淆
 */

@Keep
open class PublicResp<T>(
	var code: String? = null,
	var msg: String? = null,

	/**
	 * 返回的实体数据
	 */

	@SerializedName("data")
	var respBody: T? = null,

	) {

	fun code() = code ?: ""

	fun message() = msg ?: ""


	override fun toString(): String {
		return "PublicResp(code=$code, msg=$msg, data=$respBody)"
	}
}