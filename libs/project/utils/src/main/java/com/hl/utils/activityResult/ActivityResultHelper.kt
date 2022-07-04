package com.hl.utils.activityResult

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * @author  张磊  on  2022/07/04 at 10:47
 * Email: 913305160@qq.com
 *
 * 使用 ActivityResult 处理从 Activity  或 Fragment 中启动新的 Activity 并返回处理结果
 */
class ActivityResultHelper(private val activityResultCaller: ActivityResultCaller) {

	private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

	private val activityResultCallbackMap = hashMapOf<Class<out Activity>, OnActivityResult>()

	init {
		registerActivityResultLauncher()
	}


	/**
	 * 必须在 activity 的 onCreate中或之前调用此方法
	 */
	private fun registerActivityResultLauncher() {
		activityResultLauncher =
			activityResultCaller.registerForActivityResult(MyStartActivityForResult()) { activityResult ->

				activityResultCallbackMap.forEach { (launchActivityCls, onActivityResult) ->

					// 找到对应启动时的 Activity, 返回处理结果
					if (activityResult.data?.component?.className == launchActivityCls.name) {

						when (val resultCode = activityResult.resultCode) {
							Activity.RESULT_OK -> onActivityResult.onResultOk(activityResult.data)
							Activity.RESULT_CANCELED -> onActivityResult.onResultCanceled(activityResult.data)
							else -> onActivityResult.onResultOther(resultCode, activityResult.data)
						}
						return@forEach
					}
				}
			}
	}

	/**
	 * 启动 Activity 并返回处理结果
	 *
	 * @param launchActivityCls ： 启动的 Activity
	 * @param activityOptionsCompat ： 启动 Activity 的附加选项
	 * @param callback  ：   Activity 返回时相应的处理结果回调
	 *
	 */
	fun launchActivity(
		launchActivityCls: Class<out Activity>,
		activityOptionsCompat: ActivityOptionsCompat? = null,
		callback: OnActivityResult,
	) {
		val context: Context = when (activityResultCaller) {
			is FragmentActivity -> activityResultCaller
			is Fragment -> activityResultCaller.requireContext()
			else -> throw IllegalStateException("仅可在 Activity 或 Fragment 中支持使用")
		}

		launchIntent(Intent(context, launchActivityCls), activityOptionsCompat, callback)
	}

	/**
	 * 启动 Intent 并返回处理结果
	 *
	 * @param launchIntent ： 启动的 Activity 对应的 Intent
	 * @param activityOptionsCompat ： 启动 Intent 的附加选项
	 * @param callback  ：   Activity 返回时相应的处理结果回调
	 *
	 */
	fun launchIntent(
		launchIntent: Intent,
		activityOptionsCompat: ActivityOptionsCompat? = null,
		callback: OnActivityResult,
	) {
		launchIntent.component?.also {
			val launchActivityCls = Class.forName(it.className)
			activityResultCallbackMap[launchActivityCls as Class<out Activity>] = callback
		} ?: throw IllegalStateException("Intent 必须指定一个 component")

		activityResultLauncher.launch(launchIntent, activityOptionsCompat)
	}
}