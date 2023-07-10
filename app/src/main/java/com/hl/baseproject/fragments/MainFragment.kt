package com.hl.baseproject.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import com.elvishew.xlog.XLog
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.ShakeDetector.ShakeListener
import com.gyf.immersionbar.ktx.hideStatusBar
import com.gyf.immersionbar.ktx.showStatusBar
import com.hl.baseproject.TestActivity
import com.hl.baseproject.TestActivity2
import com.hl.baseproject.base.BaseFragment
import com.hl.baseproject.databinding.FragmentMainBinding
import com.hl.baseproject.web.WebViewNavigationFragmentDirections
import com.hl.camera.CaptureFeature
import com.hl.camera.MyCaptureActivity
import com.hl.permission.reqPermissions
import com.hl.previewfile.PreviewFileActivity
import com.hl.ui.utils.onClick
import com.hl.uikit.toast
import com.hl.unimp.UniMPHelper
import com.hl.utils.*
import com.hl.utils.navigation.findNavController
import com.lxj.xpopup.XPopup
import java.io.File


class MainFragment : BaseFragment<FragmentMainBinding>() {

	private val qrScanUtil = com.hl.qrcode.QRScanUtil(this)

	private val activityResultHelper = com.hl.activityresult.ActivityResultHelper(this)

	private val letterArray: List<String> by lazy {
		val list = ('A'..'C').map { it.toString() }.toMutableList()
		list.add("#")
		list
	}

	private val shakeListener: ShakeListener = object : ShakeListener {
		override fun onShakeDetected() {
			XLog.d("正在摇晃手机中")
		}

		override fun onShakeStopped() {
			toast("停止摇动手机")
		}
	}

	override fun onBackPressed() {
		launchHome()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requireActivity().registerReceiver(AppMainFragment.SHOW_FRAGMENT_ACTION) { _, intent ->
			if (intent.getStringExtra(AppMainFragment.SHOW_FRAGMENT_NAME_KEY) == this.javaClass.name) {
				// Navigation 回退，页面显示时恢复默认状态栏配置
				updateSystemBar()
			}
		}
	}

	override fun onResume() {
		super.onResume()
		// 页面显示时恢复默认状态栏配置
		updateSystemBar()

		Sensey.getInstance().startShakeDetection(10.0F, 1000L, shakeListener)
	}

	override fun FragmentMainBinding.onViewCreated(savedInstanceState: Bundle?) {
		// GpsUtil.checkGpsEnable(activityResultHelper) {
		// 	it.yes {
		// 		toast("GPS 已打开")
		// 	}.no {
		// 		toast("GPS 未打开")
		// 	}
		// }

		this.uikitUploadPicImageGridLayout.apply2CancelGray()

		uikitToolbar.title = "主页面主页面主页面主页面123"
		uikitToolbar.setRightActionListener {
			// toast("点击了布局文件中的操作按钮")

			val menus = arrayOf("菜单1", "菜单2", "菜单3")
			XPopup.Builder(requireContext())
				.isViewMode(true)
				.atView(it)
				.asAttachList(menus, null) { _, text ->
					toast(text)
				}
				.show()
		}

		uikitToolbar.addRightActionText("测试") {
			toast("点击测试")
		}
		uikitToolbar.addRightActionIcon(com.hl.uikit.R.drawable.uikit_ic_select_up) {
			toast("点击了后续添加的图标")
			it.context.sendBroadcast(Intent(TestActivity2.TEST_ACTION))
		}

		testScanQrcode.onClick {

			reqPermissions(Manifest.permission.CAMERA, deniedAction = {
				toast("你拒绝了权限请求呀")

			}) {
				qrScanUtil.launchDefault(scanCancelAction = {
					toast("取消扫码")
				}) {
					toast("扫描结果  == $it")
				}
			}
		}

		testRotateScreen.onClick {
			requireActivity().requestedOrientation =
				if (requireActivity().requestedOrientation == SCREEN_ORIENTATION_LANDSCAPE) {
					SCREEN_ORIENTATION_PORTRAIT
				} else {
					SCREEN_ORIENTATION_LANDSCAPE
				}
		}

		testCapture.onClick {
			reqPermissions(
				Manifest.permission.CAMERA,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.RECORD_AUDIO,
				deniedAction = {
					toast("您拒绝了相关权限，无法正常使用此功能")
				}) {

				val captureIntent = Intent(requireContext(), MyCaptureActivity::class.java).apply {
					this.putExtra(MyCaptureActivity.CAPTURE_FEATURES, CaptureFeature.BOTH)
				}
				activityResultHelper.launchIntent(captureIntent, callback = object :
					com.hl.activityresult.OnActivityResult {
					override fun onResultOk(data: Intent?) {
						val captureFilePath = data?.getStringExtra(MyCaptureActivity.CAPTURE_FILE_PATH)
						toast("拍摄后路径 == $captureFilePath")
					}
				})
			}
		}

		testFilePreview.onClick {
			val videoUrl =
				// "https://assets.mixkit.co/videos/preview/mixkit-stunning-sunset-seen-from-the-sea-4119-large.mp4"  // 横版视频
				"https://assets.mixkit.co/videos/preview/mixkit-blue-plasticine-in-the-shape-of-ice-cream-and-silver-48180-large.mp4"  // 竖版视频


			PreviewFileActivity.start(
				requireContext(),
				videoUrl.getDownloadFileNameFromUrl(),
				videoUrl
			)
		}

		testWebView.onClick {
			// this@MainFragment.navigateToWeb("http://dan520.vip/sdk/", "测试 SDK", false)
			// this@MainFragment.navigateToWeb("http://www.baidu.com", "测试 SDK", false)

			val globalWebViewFragment =
				WebViewNavigationFragmentDirections.actionGlobalWebViewNavigationFragment("http://dan520.vip/sdk/")
					.apply {
						this.title = "测试 SDK"
						this.isNeedTitle = false
					}

			findNavController().navigate(globalWebViewFragment)
		}

		gotoTest1.onClick {
			activityResultHelper.launchActivity(TestActivity::class.java, callback = object :
				com.hl.activityresult.OnActivityResult {
				override fun onResultOk(data: Intent?) {
					toast(data?.getStringExtra("data") ?: "")
				}
			})
		}

		gotoTest2.onClick {
			activityResultHelper.launchActivity(TestActivity2::class.java, callback = object :
				com.hl.activityresult.OnActivityResult {
				override fun onResultOk(data: Intent?) {
					toast(data?.getStringExtra("data") ?: "")
				}

				override fun onResultCanceled(data: Intent?) {
					toast("取消")
				}
			})
		}

		startShadowPlugin.onClick {
			findNavController().navigate(AppMainFragmentDirections.actionAppMainFragmentToShadowPluginFragment())
		}

		startUniMP.onClick {
			val appid = "__UNI__0773E11"

			UniMPHelper.openUniMPFromWgt(requireContext(), appid, uniMPReleaseConfigurationBlock = {
				this.wgtPath = File(Environment.getExternalStorageDirectory(), "${appid}.wgt").absolutePath
			})
		}

		startCompose.onClick {
			findNavController().navigate(AppMainFragmentDirections.actionAppMainFragmentToComposeGraph())
		}
	}

	override fun onPause() {
		super.onPause()
		Sensey.getInstance().stopShakeDetection(shakeListener)
	}


	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// setImmersiveSystemBar(true)
			hideStatusBar()
		} else {
			// setImmersiveSystemBar(false)
			showStatusBar()
		}
	}

	@Deprecated("Deprecated in Java")
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		val captureFilePath = data?.getStringExtra(MyCaptureActivity.CAPTURE_FILE_PATH)
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			toast("拍摄后路径 == $captureFilePath")
		}
	}
}