package com.hl.baseproject.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.lzyzsd.jsbridge.BridgeHandler
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.hl.arch.mvvm.fragment.ViewBindingMvvmBaseFragment
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.arch.mvvm.vm.LiveDataVM
import com.hl.arch.web.helpers.JsBridgeHelper
import com.hl.arch.web.navigateToWeb
import com.hl.arch.web.sdk.ISdk
import com.hl.arch.web.sdk.ISdkImplProvider
import com.hl.baseproject.TestActivity
import com.hl.baseproject.TestActivity2
import com.hl.baseproject.databinding.FragmentMainBinding
import com.hl.shadow.Shadow
import com.hl.shadow.lib.ShadowConstants
import com.hl.uikit.onClick
import com.hl.uikit.toast
import com.hl.utils.ProxyHandler
import com.hl.utils.activityResult.ActivityResultHelper
import com.hl.utils.activityResult.OnActivityResult
import com.hl.utils.camera.CaptureFeature
import com.hl.utils.camera.MyCaptureActivity
import com.hl.utils.copyAssets2Path
import com.hl.utils.previewFie.PreviewFileActivity
import com.hl.utils.qrcode.QRScanUtil
import com.hl.utils.reqPermissions
import com.hl.utils.setImmersiveSystemBar
import com.lxj.xpopup.XPopup
import com.tencent.shadow.dynamic.host.EnterCallback
import java.io.File


class MainFragment : ViewBindingMvvmBaseFragment<FragmentMainBinding>() {

	override fun isActivityMainPage(): Boolean {
		return false
	}

	private val qrScanUtil = QRScanUtil(this)

	private val activityResultHelper = ActivityResultHelper(this)

	private val letterArray: List<String> by lazy {
		val list = ('A'..'C').map { it.toString() }.toMutableList()
		list.add("#")
		list
	}

	override fun onLiveDataVMCreated(liveDataVM: LiveDataVM) {
	}

	override fun onFlowVMCreated(flowVM: FlowVM) {
	}

	override fun FragmentMainBinding.onViewCreated(savedInstanceState: Bundle?) {
		uikitToolbar.title = "主页面主页面主页面主页面123"
		uikitToolbar.setRightActionListener {
			toast("点击了布局文件中的操作按钮")
		}

		uikitToolbar.addRightActionText("测试") {
			toast("点击测试")
		}
		uikitToolbar.addRightActionIcon(com.hl.uikit.R.drawable.uikit_ic_select_up) {
			toast("点击了后续添加的图标")
		}

		testScanQrcode.onClick {

			reqPermissions(android.Manifest.permission.CAMERA, deniedAction = {
				toast("你拒绝了权限请求呀")

			}) {
				qrScanUtil.launchDefault(scanCancelAction = {
					toast("取消扫码")
				}) {
					toast("扫描结果  == $it")
				}
			}
		}

		sideBar.setLetters(letterArray)


		testRotateScreen.onClick {
			requireActivity().requestedOrientation =
				if (requireActivity().requestedOrientation == SCREEN_ORIENTATION_LANDSCAPE) {
					SCREEN_ORIENTATION_PORTRAIT
				} else {
					SCREEN_ORIENTATION_LANDSCAPE
				}
		}

		testCapture.onClick {
			MyCaptureActivity.start(requireActivity(), CaptureFeature.BOTH, 1)
		}

		testFilePreview.onClick {
			PreviewFileActivity.start(
				requireContext(),
				"Enigma_Principles_of_Lust-part.flv",
				"http://samples.mplayerhq.hu/FLV/Enigma_Principles_of_Lust-part.flv"
			)
		}

		JsBridgeHelper.setISdkImplProvider(object : ISdkImplProvider() {
			override fun provideProjectSdkImpl(
				currentFragment: Fragment,
				bridgeWebView: BridgeWebView,
				bridgeHandlerProxy: ProxyHandler<BridgeHandler>
			): ISdk {
				return object : ISdk {

				}
			}
		})
		testWebView.onClick {
			this@MainFragment.navigateToWeb("http://192.168.3.18:9003", "测试 SDK", false)
			// this@MainFragment.navigateToWeb("http://www.baidu.com", "测试 SDK", false)
		}

		gotoTest1.onClick {
			activityResultHelper.launchActivity(TestActivity::class.java, callback = object : OnActivityResult {
				override fun onResultOk(data: Intent?) {
					toast(data?.getStringExtra("data") ?: "")
				}
			})
		}

		gotoTest2.onClick {
			activityResultHelper.launchActivity(TestActivity2::class.java, callback = object : OnActivityResult {
				override fun onResultOk(data: Intent?) {
					toast(data?.getStringExtra("data") ?: "")
				}
			})
		}

		startShadowPlugin.onClick {
			showSelectShadowPluginDialog()
		}
	}


	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setImmersiveSystemBar(true)
		} else {
			setImmersiveSystemBar(false)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		val captureFilePath = data?.getStringExtra(MyCaptureActivity.CAPTURE_FILE_PATH)
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			toast("拍摄后路径 == $captureFilePath")
		}
	}

	private fun showSelectShadowPluginDialog() {
		val items = listOf(
			"启动 SunFlower 插件",
			"启动自定义测试插件",
			"启动优码测试插件",
			"启动依赖库的Service",
			"启动自定义Service",
			"启动自定义IntentService",
		)

		XPopup.Builder(requireContext()).asCenterList("启动 Shadow 插件", items.toTypedArray()) { position, _ ->

			val bundle = Bundle().apply {
				// 插件 zip 的路径
				val pluginSavePath =
					File(requireContext().getExternalFilesDir(null), "plugins/plugin-release.zip").absolutePath
				val pluginZipPath =
					requireContext().copyAssets2Path("plugins/plugin-release.zip", pluginSavePath)

				putString(ShadowConstants.KEY_PLUGIN_ZIP_PATH, pluginZipPath)
			}

			when (position) {
				0 -> {
					//启动插件中的对应的 Activity
					bundle.putString(
						ShadowConstants.KEY_CLASSNAME,
						"com.google.samples.apps.sunflower.GardenActivity"
					)

					// partKey 每个插件都有自己的 partKey 用来区分多个插件，需要与插件打包脚本中的 packagePlugin{ partKey xxx} 一致
					bundle.putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "sunflower")
					bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_START_ACTIVITY)
				}
				1 -> {
					bundle.putString(ShadowConstants.KEY_CLASSNAME, "com.hl.myplugin.MainActivity")
					bundle.putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "test")
					bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_START_ACTIVITY)
				}
				2 -> {
					val pluginSavePath =
						File(
							requireContext().getExternalFilesDir(null),
							"plugins/plugin-cjsxt-release.zip"
						).absolutePath
					val pluginZipPath =
						requireContext().copyAssets2Path("plugins/plugin-cjsxt-release.zip", pluginSavePath)
					bundle.putString(ShadowConstants.KEY_PLUGIN_ZIP_PATH, pluginZipPath)

					bundle.apply {
						putString(ShadowConstants.KEY_CLASSNAME, "com.youma.cjspro.moudle.login.LoginActivity")

						// partKey 每个插件都有自己的 partKey 用来区分多个插件，需要与插件打包脚本中的 packagePlugin{ partKey xxx} 一致
						putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "cjsxt")
					}
					bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_START_ACTIVITY)
				}
				3 -> {
					bundle.putString(
						ShadowConstants.KEY_CLASSNAME,
						"com.tsinglink.android.update.CheckUpdateIntentService"
					)
					bundle.putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "test")
					bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_CALL_SERVICE)
					bundle.putString(
						ShadowConstants.KEY_INTENT_ACTION,
						"com.tsinglink.android.update.ACTION_START_DOWNLOAD"
					)
					bundle.putBundle(ShadowConstants.KEY_EXTRAS, Bundle().apply {
						this.putString(
							"com.tsinglink.android.update.extra.DOWNLOAD_URL", "http://down.qq" +
									".com/qqweb/QQ_1/android_apk/Androidqq_8.4.10.4875_537065980.apk"
						)
					})
				}
				4 -> {
					bundle.putString(ShadowConstants.KEY_CLASSNAME, "com.hl.myplugin.TestService")
					bundle.putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "test")
					bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_CALL_SERVICE)
				}
				5 -> {

					// val receiver: ResultReceiver = TestResultReceiver(Handler(Looper.getMainLooper()))
					//
					// bundle.putString(ShadowConstants.KEY_CLASSNAME, "com.hl.myplugin.TestIntentService")
					// bundle.putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "test")
					// bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_CALL_SERVICE)
					//
					// bundle.putString(
					// 	ShadowConstants.KEY_INTENT_ACTION,
					// 	"com.hl.myplugin.action.FOO"
					// )
					// bundle.putBundle(ShadowConstants.KEY_EXTRAS, Bundle().apply {
					// 	this.putString("com.hl.myplugin.extra.PARAM1", "我是参数1")
					// 	this.putParcelable("com.hl.myplugin.extra.PARAM2", receiver)
					// })
				}
			}

			if (bundle.getBundle(ShadowConstants.KEY_EXTRAS) == null) {
				bundle.putBundle(ShadowConstants.KEY_EXTRAS, Bundle().apply {
					this.putString("测试数据", "我是宿主传过来的数据")
				})
			}

			val permissions = arrayOf(
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
			)
			this@MainFragment.reqPermissions(*permissions, allGrantedAction = {
				startShadowPlugin(requireContext(), bundle)
			})
		}.show()
	}

	private fun startShadowPlugin(context: Context, bundle: Bundle) {
		val pluginManager = Shadow.getMyPluginManager(context)

		/**
		 * context context
		 * formId  标识本次请求的来源位置，用于区分入口
		 * bundle  参数列表, 建议在参数列表加入自己的验证
		 * callback 用于从PluginManager实现中返回View
		 */
		pluginManager?.enter(context, bundle.getLong(ShadowConstants.KEY_FROM_ID), bundle, object : EnterCallback {
			override fun onShowLoadingView(view: View?) {}
			override fun onCloseLoadingView() {}
			override fun onEnterComplete() {
				// 启动成功
				toast("启动成功")
			}
		})
	}

}