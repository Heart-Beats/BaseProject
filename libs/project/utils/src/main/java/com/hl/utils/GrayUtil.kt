package com.hl.utils

import android.app.Activity
import android.app.Application
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.elvishew.xlog.XLog

/**
 * @author  张磊  on  2022/12/07 at 10:47
 * Email: 913305160@qq.com
 *
 * 对 View、Activity 或者 Application 设置【灰度/取消灰度】效果
 */
object GrayUtil {

	private fun getPaint(isGray: Boolean): Paint {
		val paint = Paint()
		//设置颜色饱和度， 0 时即为 灰色
		val saturation = if (isGray) 0F else 1F

		val colorMatrix = ColorMatrix().apply {
			setSaturation(saturation)
		}
		paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
		return paint
	}

	private fun getHandleDesc(isGray: Boolean): String {
		return if (isGray) "灰度" else "取消灰度"
	}

	/**
	 * 对 View 进行【灰度/取消灰度】处理
	 */
	@JvmStatic
	fun apply2View(view: View, isGray: Boolean) {
		XLog.d("开始进行${getHandleDesc(isGray)}处理 ---------> $view")
		view.setLayerType(View.LAYER_TYPE_HARDWARE, getPaint(isGray))
	}

	/**
	 * 对 fragment 进行【灰度/取消灰度】处理
	 *
	 *    注意： 若 Activity 已置灰，对 fragment 取消置灰会无效
	 */
	@JvmStatic
	fun apply2Fragment(fragment: Fragment, isGray: Boolean) {
		fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
			override fun onCreate(owner: LifecycleOwner) {
				XLog.d("开始进行${getHandleDesc(isGray)}处理 ---------> $fragment")
				apply2View(fragment.requireView(), isGray)
			}

			override fun onDestroy(owner: LifecycleOwner) {
				fragment.lifecycle.removeObserver(this)
			}
		})
	}

	/**
	 * 对 Activity 进行【灰度/取消灰度】处理
	 */
	@JvmStatic
	fun apply2Activity(activity: Activity, isGray: Boolean) {
		when (activity) {
			is ComponentActivity -> {
				activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
					override fun onCreate(owner: LifecycleOwner) {
						XLog.d("开始进行${getHandleDesc(isGray)}处理 ---------> $activity")
						apply2View(activity.window.decorView, isGray)
					}

					override fun onDestroy(owner: LifecycleOwner) {
						activity.lifecycle.removeObserver(this)
					}
				})
			}
			else -> {
				XLog.d("开始进行${getHandleDesc(isGray)}处理 ---------> $activity")
				apply2View(activity.window.decorView, isGray)
			}
		}
	}

	/**
	 * 对 Application 进行【灰度/取消灰度】处理
	 */
	@JvmStatic
	fun apply2Application(application: Application, isGray: Boolean) {
		XLog.d("开始进行${getHandleDesc(isGray)}处理 ---------> $application")
		application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
			override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
				apply2Activity(activity, isGray)
			}

			override fun onActivityStarted(activity: Activity) {
			}

			override fun onActivityResumed(activity: Activity) {
			}

			override fun onActivityPaused(activity: Activity) {
			}

			override fun onActivityStopped(activity: Activity) {
			}

			override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
			}

			override fun onActivityDestroyed(activity: Activity) {
			}
		})
	}
}

/**
 * 对 View 进行灰度处理
 */
fun View.apply2Gray() {
	GrayUtil.apply2View(this, true)
}

/**
 * 对 Fragment 进行灰度处理
 */
fun Fragment.apply2Gray() {
	GrayUtil.apply2Fragment(this, true)
}

/**
 * 对 Activity 进行灰度处理
 */
fun Activity.apply2Gray() {
	GrayUtil.apply2Activity(this, true)
}

/**
 * 对 Application 进行灰度处理
 */
fun Application.apply2Gray() {
	GrayUtil.apply2Application(this, true)
}

/**
 * 对 View 取消灰度处理
 */
fun View.apply2CancelGray() {
	GrayUtil.apply2View(this, false)
}

/**
 * 对 Fragment 取消灰度处理
 */
fun Fragment.apply2CancelGray() {
	GrayUtil.apply2Fragment(this, false)
}

/**
 * 对 Activity 取消灰度处理
 */
fun Activity.apply2CancelGray() {
	GrayUtil.apply2Activity(this, false)
}

/**
 * 对 Application 取消灰度处理
 */
fun Application.apply2CancelGray() {
	GrayUtil.apply2Application(this, false)
}