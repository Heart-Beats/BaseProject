package com.hl.utils

import android.app.Activity
import android.app.Application
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.elvishew.xlog.XLog

/**
 * @author  张磊  on  2022/12/07 at 10:47
 * Email: 913305160@qq.com
 *
 * 对 View、Activity 或者 Application 设置灰度效果
 */
object GrayUtil {

	private var paint: Paint? = null

	init {
		paint = Paint()
		val colorMatrix = ColorMatrix().apply {
			//设置灰度效果
			setSaturation(0f)
		}
		paint?.colorFilter = ColorMatrixColorFilter(colorMatrix)
	}

	/**
	 * 对 View 进行灰度处理
	 */
	@JvmStatic
	fun apply2View(view: View) {
		XLog.d("开始进行灰度处理 ---------> $view")
		view.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
	}

	/**
	 * 对 Activity 进行灰度处理
	 */
	@JvmStatic
	fun apply2Activity(activity: Activity) {
		when (activity) {
			is ComponentActivity -> {
				activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
					override fun onCreate(owner: LifecycleOwner) {
						XLog.d("开始进行灰度处理 ---------> $activity")
						apply2View(activity.window.decorView)
					}

					override fun onDestroy(owner: LifecycleOwner) {
						activity.lifecycle.removeObserver(this)
					}
				})
			}
			else -> {
				XLog.d("开始进行灰度处理 ---------> $activity")
				apply2View(activity.window.decorView)
			}
		}
	}

	/**
	 * 对 Application 进行灰度处理
	 */
	@JvmStatic
	fun apply2Application(application: Application) {
		XLog.d("开始进行灰度处理 ---------> $application")
		application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
			override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
				apply2Activity(activity)
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
fun View.apply2gray() {
	GrayUtil.apply2View(this)
}

/**
 * 对 Activity 进行灰度处理
 */
fun Activity.apply2gray() {
	GrayUtil.apply2Activity(this)
}

/**
 * 对 Application 进行灰度处理
 */
fun Application.apply2gray() {
	GrayUtil.apply2Application(this)
}