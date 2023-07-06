package com.hl.web

import android.os.Bundle

/**
 * @author 张磊  on  2022/06/13 at 11:11
 * Email: 913305160@qq.com
 */
class WebViewFragmentArgs {
	private val arguments: HashMap<Any?, Any?>

	private constructor() {
		arguments = HashMap()
	}

	private constructor(argumentsMap: HashMap<Any?, Any?>) {
		val hashMap = HashMap<Any?, Any?>()
		arguments = hashMap
		hashMap.putAll(argumentsMap)
	}

	val title: String?
		get() = arguments["title"] as String?

	val url: String
		get() = arguments["url"] as String

	val isNeedTitle: Boolean
		get() = arguments["isNeedTitle"] as Boolean

	fun toBundle(): Bundle {
		val __result = Bundle()
		if (arguments.containsKey("title")) {
			__result.putString("title", arguments["title"] as String?)
		} else {
			__result.putString("title", null)
		}
		if (arguments.containsKey("url")) {
			__result.putString("url", arguments["url"] as String?)
		}
		if (arguments.containsKey("isNeedTitle")) {
			__result.putBoolean("isNeedTitle", arguments["isNeedTitle"] as Boolean)
		} else {
			__result.putBoolean("isNeedTitle", false)
		}
		return __result
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}
		if (other == null || javaClass != other.javaClass) {
			return false
		}
		val that = other as WebViewFragmentArgs
		if (arguments.containsKey("title") != that.arguments.containsKey("title")) {
			return false
		}
		if (if (title == null) that.title != null else title != that.title) {
			return false
		}
		if (arguments.containsKey("url") != that.arguments.containsKey("url")) {
			return false
		}
		if (url != that.url) {
			return false
		}
		return arguments.containsKey("isNeedTitle") == that.arguments.containsKey("isNeedTitle") && isNeedTitle == that.isNeedTitle
	}

	override fun hashCode(): Int {
		val result = (1 * 31 + if (title != null) title.hashCode() else 0) * 31
		val i: Int = url.hashCode()
		return (result + i) * 31 + if (isNeedTitle) 1 else 0
	}

	override fun toString(): String {
		return "WebViewFragmentArgs{title=$title, url=$url, isNeedTitle=$isNeedTitle"
	}

	class Builder(url: String) {

		private val arguments = HashMap<Any?, Any?>()

		init {
			arguments["url"] = url
		}

		fun build(): WebViewFragmentArgs {
			return WebViewFragmentArgs(arguments)
		}

		fun setTitle(title: String?): Builder {
			arguments["title"] = title
			return this
		}

		fun setUrl(url: String): Builder {
			arguments["url"] = url
			return this
		}

		fun setIsNeedTitle(isNeedTitle: Boolean): Builder {
			arguments["isNeedTitle"] = isNeedTitle
			return this
		}
	}

	companion object {

		fun fromBundle(bundle: Bundle): WebViewFragmentArgs {
			val __result = WebViewFragmentArgs()
			bundle.classLoader = WebViewFragmentArgs::class.java.classLoader
			if (bundle.containsKey("title")) {
				__result.arguments["title"] = bundle.getString("title")
			} else {
				__result.arguments["title"] = null
			}
			if (bundle.containsKey("url")) {
				val url = bundle.getString("url")
				if (url != null) {
					__result.arguments["url"] = url
					if (bundle.containsKey("isNeedTitle")) {
						__result.arguments["isNeedTitle"] = bundle.getBoolean("isNeedTitle")
					} else {
						__result.arguments["isNeedTitle"] = false
					}
					return __result
				}
				throw IllegalArgumentException("Argument \"url\" is marked as non-null but was passed a null value.")
			}
			throw IllegalArgumentException("Required argument \"url\" is missing and does not have an android:defaultValue")
		}
	}
}