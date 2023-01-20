package com.hl.utils.camera

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.cjt2325.cameralibrary.JCameraView
import com.cjt2325.cameralibrary.listener.ErrorListener
import com.cjt2325.cameralibrary.listener.JCameraListener
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar
import com.hl.uikit.image.pictureselector.MyCompressEngine
import com.hl.uikit.toast
import com.hl.utils.R
import com.hl.utils.reqPermissions
import com.hl.utils.startActForResult
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * 点击拍照，长按录像
 */
class MyCaptureActivity : FragmentActivity() {

	companion object {
		const val CAPTURE_FILE_PATH = "captureFilePath"

		private const val TAG = "MyCaptureActivity"
		const val CAPTURE_FEATURES = "CAPTURE_FEATURES"

		@JvmStatic
		fun start(activity: FragmentActivity, captureFeature: CaptureFeature, reqCode: Int) {

			activity.reqPermissions(
				Manifest.permission.CAMERA,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.RECORD_AUDIO,
				deniedAction = {
					activity.toast("您拒绝了相关权限，无法正常使用此功能")
				}) {
				activity.startActForResult<MyCaptureActivity>(reqCode) {
					putExtra(CAPTURE_FEATURES, captureFeature)
				}
			}
		}
	}

	private lateinit var jCameraView: JCameraView

	private lateinit var defaultCaptureDir: String

	private var captureFilePath: String? = null


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// 页面退出时隐藏系统栏
		immersionBar {
			this.hideBar(BarHide.FLAG_HIDE_BAR)
		}

		setContentView(R.layout.hl_utils_activity_my_capture)

		initView()
	}

	override fun onResume() {
		super.onResume()
		jCameraView.onResume()
	}

	override fun onPause() {
		super.onPause()
		jCameraView.onPause()
	}

	override fun onDestroy() {
		// 页面退出时恢复系统栏
		immersionBar {
			this.hideBar(BarHide.FLAG_SHOW_BAR)
		}

		super.onDestroy()
	}


	private fun initView() {
		getExternalFilesDir(null)?.path?.also {
			defaultCaptureDir = it + File.separator + "capture"

			//进来默认删除上次保存的图片和视频
			FileUtils.delete(defaultCaptureDir)

			initJCameraView()

		} ?: toast("获取保存路径失败")
	}


	private fun initJCameraView() {
		jCameraView = findViewById(R.id.jCamera_view)

		//设置视频保存路径
		jCameraView.setSaveVideoPath(defaultCaptureDir)

		val (featureState, tip) = when (intent.getSerializableExtra(CAPTURE_FEATURES) as CaptureFeature) {
			CaptureFeature.ONLY_CAPTURE -> Pair(JCameraView.BUTTON_STATE_ONLY_CAPTURE, "轻触拍照")
			CaptureFeature.ONLY_RECORD -> Pair(JCameraView.BUTTON_STATE_ONLY_RECORDER, "长按摄像")
			CaptureFeature.BOTH -> Pair(JCameraView.BUTTON_STATE_BOTH, "轻触拍照，长按摄像")
		}

		//设置只能录像或只能拍照或两种都可以（默认两种都可以）
		jCameraView.setFeatures(featureState)

		jCameraView.setTip(tip)

		//设置视频质量
		jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE)

		//JCameraView监听
		jCameraView.setErrorLisenter(object : ErrorListener {
			override fun onError() {
				//打开Camera失败回调
				toast("打开相机失败")
			}

			override fun AudioPermissionError() {
				//没有录取权限回调
				toast("请确保已授予录音权限")
			}
		})

		jCameraView.setJCameraLisenter(object : JCameraListener {
			override fun captureSuccess(bitmap: Bitmap) {
				//获取图片bitmap
				val imageSaveFile = File(defaultCaptureDir, getCurrentTime() + ".png")

				if (ImageUtils.save(bitmap, imageSaveFile, Bitmap.CompressFormat.PNG)) {
					Log.d(TAG, "captureSuccess: 照片保存路径 == ${imageSaveFile.absolutePath}")

					MyCompressEngine().startCompress(this@MyCaptureActivity, listOf(imageSaveFile.absolutePath)) {

						captureFilePath = it.firstOrNull()
						resultFinish()
					}
				} else {
					toast("保存照片失败")
				}
			}

			override fun recordSuccess(url: String, firstFrame: Bitmap) {
				//获取视频路径
				Log.d(TAG, "recordSuccess: 视频保存路径 == $url")
				captureFilePath = url

				resultFinish()
			}
		})

		//左边按钮点击事件
		jCameraView.setLeftClickListener {
			finish()
		}

		//右边按钮点击事件,  必须设置 iconRight 属性该按钮才可见
		jCameraView.setRightClickListener {

		}
	}

	private fun resultFinish() {
		setResult(RESULT_OK, Intent().apply {
			this.putExtra(CAPTURE_FILE_PATH, captureFilePath)
		})
		finish()
	}


	private fun getCurrentTime(): String {
		val formatter = SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault())
		val curDate = Date(System.currentTimeMillis())
		return formatter.format(curDate)
	}

}