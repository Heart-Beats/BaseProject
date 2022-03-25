package com.hl.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.whenCreated
import androidx.lifecycle.whenStateAtLeast
import kotlinx.coroutines.launch
import kotlin.math.abs


/**
 * @author  张磊  on  2021/05/06 at 11:15
 * Email: 913305160@qq.com
 */
class OrientationSensorHelper(val context: Context, lifecycle: Lifecycle, private val angleListener: (x: Float, y: Float, z: Float) -> Unit) : SensorEventListener {

	private val mSensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

	private val mSensor: Sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
	private var lastTime: Long = 0
	private val time_sensor = 100
	private var mAngle = 0f

	init {
		lifecycle.coroutineScope.launch {
			lifecycle.whenCreated {
				mSensorManager.registerListener(this@OrientationSensorHelper, mSensor, SensorManager.SENSOR_DELAY_NORMAL)
			}
			lifecycle.whenStateAtLeast(Lifecycle.State.DESTROYED) {
				mSensorManager.unregisterListener(this@OrientationSensorHelper, mSensor)
			}
		}
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
		// Auto-generated method stub
	}

	override fun onSensorChanged(event: SensorEvent) {
		if (System.currentTimeMillis() - lastTime < time_sensor) {
			return
		}
		when (event.sensor.type) {
			Sensor.TYPE_ORIENTATION -> {
				var x = event.values[0]
				x += getScreenRotationOnPhone(context).toFloat()
				x %= 360.0f
				if (x > 180.0f) x -= 360.0f else if (x < -180.0f) x += 360.0f
				if (abs(mAngle - x) < 3.0f) {
					return
				}
				mAngle = if (java.lang.Float.isNaN(x)) 0F else x

				angleListener(x, event.values[1], event.values[2])
				lastTime = System.currentTimeMillis()
			}
		}
	}

	/**
	 * 获取当前屏幕旋转角度
	 *
	 * @param context
	 * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
	 */
	private fun getScreenRotationOnPhone(context: Context): Int {
		val display = context.display
		when (display?.rotation) {
			Surface.ROTATION_0 -> return 0
			Surface.ROTATION_90 -> return 90
			Surface.ROTATION_180 -> return 180
			Surface.ROTATION_270 -> return -90
		}
		return 0
	}
}