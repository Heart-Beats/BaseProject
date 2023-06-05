package com.hl.baseproject.compose.utils

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import java.io.Serializable

/**
 * @author  张磊  on  2023/06/05 at 14:56
 * Email: 913305160@qq.com
 */


/**
 * 创建自定义的 Parcelable 导航参数
 */
inline fun <reified D : Parcelable> parcelableNavType(isNullableAllowed: Boolean = true): NavType<D> {
	return object : NavType<D>(isNullableAllowed) {

		override val name: String = "SupportParcelable"


		override fun put(bundle: Bundle, key: String, value: D) {
			//作为 Parcelable 类型添加到 Bundle
			bundle.putParcelable(key, value)
		}

		override fun get(bundle: Bundle, key: String): D? {
			//从Bundle中检索 Parcelable类型
			return bundle.getParcelable(key)
		}

		override fun parseValue(value: String): D {
			//定义传递给 String 的 Parsing 方法，复杂数据必须转化为 Json 字符串
			return Gson().fromJson(value, D::class.java)
		}
	}
}


/**
 * 创建自定义的 Serializable 导航参数
 */
inline fun <reified D : Serializable> serializableNavType(isNullableAllowed: Boolean = true): NavType<D> {
	return object : NavType<D>(isNullableAllowed) {

		override val name: String = "SupportSerializable"

		override fun put(bundle: Bundle, key: String, value: D) {
			//作为 Serializable 类型添加到 Bundle
			bundle.putSerializable(key, value)
		}

		override fun get(bundle: Bundle, key: String): D? {
			//从Bundle中检索 Serializable类型
			return bundle.getSerializable(key) as? D
		}

		override fun parseValue(value: String): D {
			//定义传递给 String 的 Parsing 方法，复杂数据必须转化为 Json 字符串
			return Gson().fromJson(value, D::class.java)
		}
	}
}