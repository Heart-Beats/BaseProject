package com.hl.baseproject.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.lzyzsd.jsbridge.BridgeHandler
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.hl.arch.mvvm.activity.startFragment
import com.hl.arch.web.helpers.JsBridgeHelper
import com.hl.arch.web.navigateToWeb
import com.hl.arch.web.sdk.ISdk
import com.hl.arch.web.sdk.ISdkImplProvider
import com.hl.baseproject.TestActivity
import com.hl.baseproject.TestActivity2
import com.hl.baseproject.databinding.FragmentMainBinding
import com.hl.baseproject.fragments.base.BaseFragment
import com.hl.uikit.onClick
import com.hl.uikit.toast
import com.hl.utils.*
import com.hl.utils.activityResult.ActivityResultHelper
import com.hl.utils.activityResult.OnActivityResult
import com.hl.utils.camera.CaptureFeature
import com.hl.utils.camera.MyCaptureActivity
import com.hl.utils.previewFie.PreviewFileActivity
import com.hl.utils.qrcode.QRScanUtil
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnSelectListener


class MainFragment : BaseFragment<FragmentMainBinding>() {

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

	override fun FragmentMainBinding.onViewCreated(savedInstanceState: Bundle?) {
		this.uikitUploadPicImageGridLayout.apply2CancelGray()

		uikitToolbar.title = "主页面主页面主页面主页面123"
		uikitToolbar.setRightActionListener {
			// toast("点击了布局文件中的操作按钮")

			val menus = arrayOf("菜单1", "菜单2", "菜单3")
			XPopup.Builder(requireContext())
				.isViewMode(true)
				.atView(it)
				.asAttachList(menus, null, object : OnSelectListener {
					override fun onSelect(position: Int, text: String?) {
						toast("$text")
					}
				})
				.show()
		}

		uikitToolbar.addRightActionText("测试") {
			toast("点击测试")
		}
		uikitToolbar.addRightActionIcon(com.hl.uikit.R.drawable.uikit_ic_select_up) {
			toast("点击了后续添加的图标")
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

				override fun onResultCanceled(data: Intent?) {
					toast("取消")
				}
			})
		}

		startShadowPlugin.onClick {
			startFragment(ShadowPluginFragment::class.java)
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
}