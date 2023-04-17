package com.hl.arch.web.sdk

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.NetworkUtils
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.http.listener.OnDownloadListener
import com.hl.arch.base.FragmentContainerActivity
import com.hl.arch.base.startFragment
import com.hl.arch.web.H5Constants
import com.hl.arch.web.WebViewFragment
import com.hl.arch.web.WebViewFragmentArgs
import com.hl.arch.web.bean.*
import com.hl.arch.web.helpers.H5DataHelper
import com.hl.arch.web.helpers.logJs
import com.hl.arch.web.helpers.onFail
import com.hl.arch.web.helpers.onSuccess
import com.hl.arch.web.receiver.CallBackFunctionDataStore
import com.hl.arch.web.receiver.CallBackFunctionHandlerReceiver
import com.hl.uikit.getStatusBarHeight
import com.hl.umeng.sdk.MyUMShareListener
import com.hl.umeng.sdk.UMShareUtil
import com.hl.utils.*
import com.hl.utils.activityResult.OnActivityResult
import com.hl.utils.date.getFormattedNowDateTime
import com.hl.utils.navigation.findNavController
import com.hl.utils.previewFie.PreviewFileActivity
import com.king.zxing.CameraScan
import com.king.zxing.CaptureActivity
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.SmartGlideImageLoader
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

/**
 * @author  张磊  on  2022/06/13 at 15:14
 * Email: 913305160@qq.com
 */

class IStandSdkImpl(private val webViewFragment: Fragment, val bridgeWebView: BridgeWebView) :
	IStandSdk, ISdkRegister by ISdkHandlerProxy(bridgeWebView) {

	/**
	 * 当前 WebViewFragment 依附的 Activity
	 */
	private val attachActivity by lazy { webViewFragment.requireActivity() }

	init {
		webViewFragment.lifecycle.addObserver(object : DefaultLifecycleObserver {

			override fun onCreate(owner: LifecycleOwner) {
				val intentFilter = IntentFilter(H5Constants.ACTION_CALL_BACK)

				// 注册 CallBackFunction 需要单独处理的广播
				webViewFragment.requireContext().registerReceiver(CallBackFunctionHandlerReceiver, intentFilter)
			}

			override fun onResume(owner: LifecycleOwner) {
				logJs("onH5Show", "")
				// 通知 h5 页面可见
				bridgeWebView.callHandler("onH5Show", "") { }
			}

			override fun onPause(owner: LifecycleOwner) {
				logJs("onH5Hide", "")
				// 通知 h5 页面不可见
				bridgeWebView.callHandler("onH5Hide", "") { }
			}

			override fun onDestroy(owner: LifecycleOwner) {
				// 反注册 CallBackFunction 需要单独处理的广播
				webViewFragment.requireContext().unregisterReceiver(CallBackFunctionHandlerReceiver)
				CallBackFunctionDataStore.clearCallBackFunction()
				webViewFragment.lifecycle.removeObserver(this)
			}
		})
	}

	override fun getDeviceInfo(handlerName: String) {
		commonRegisterHandler(handlerName) { _, function ->

			// h5 获取的状态栏高度需要除以 density
			val statusBarHeightDp =
				bridgeWebView.getStatusBarHeight() / bridgeWebView.resources.displayMetrics.density

			val isWifi = if (NetworkUtils.isWifiConnected()) 1 else 0
			val deviceInfo =
				H5DeviceInfo(AndroidDeviceInfo(statusBarHeight = statusBarHeightDp.toInt(), isWifi = isWifi))
			function.onSuccess(deviceInfo)
		}
	}

	override fun navigateBack(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			bridgeWebView.h5NavigateBack(data, function)
		}
	}

	private fun BridgeWebView.h5NavigateBack(data: String?, function: CallBackFunction) {
		val h5NavigateBackParam = GsonUtil.fromJson<H5NavigateBackParam>(data) ?: return
		repeat(h5NavigateBackParam.step) {

			logJs("h5NavigateBack", "开始")

			// h5 页面本身可回退时， 作自身的回退， 不可回退时关闭原生页面
			if (bridgeWebView.canGoBack()) {
				bridgeWebView.goBack()
			} else {
				// 获取栈顶的 Activity， 即当前的 Activity
				val currentActivity = ActivityUtils.getTopActivity()
				if (currentActivity.isWebViewContainerActivity()) {
					currentActivity.finish()
				} else if (currentActivity.isWebViewNavigationActivity()) {
					logJs("h5NavigateBack", "开始回退")
					findNavController().popBackStack()
				}
			}
		}

		MainScope().launch {

			//延迟处理刷新相关逻辑，防止返回时当前页面未销毁时，获取到的 webViewFragment 不对
			delay(50)

			if (!h5NavigateBackParam.isNeedRefresh()) return@launch

			var webViewFragment: Fragment? = null

			// 回退之后再获取栈顶 Activity，根据不同打开 H5 页面方式，获取当前页面的 webViewFragment 并刷新 WebView
			val currentActivity = ActivityUtils.getTopActivity() as FragmentActivity

			when {
				currentActivity.isWebViewContainerActivity() -> webViewFragment =
					currentActivity.supportFragmentManager.findFragmentByTag(WebViewFragment::class.java.simpleName)
				currentActivity.isWebViewNavigationActivity() -> webViewFragment =
					currentActivity.getCurrentNavigationFragment()
			}

			val webView =
				webViewFragment?.requireView()?.traverseFindFirstViewByType(WebView::class.java)

			webView?.also {
				it.reload()
				function.onSuccess("页面刷新成功")
			} ?: function.onFail("当前页面未找到 webView ")
		}
	}

	/**
	 * 判断 WebView  是否在 FragmentContainerActivity 中打开的
	 */
	private fun Activity.isWebViewContainerActivity(): Boolean {
		logJs(
			"isWebViewContainerActivity", "当前的 Activity ==${this}, " +
					"当前 Activity 显示的 WebViewFragment ==${
						(this as? FragmentActivity)?.supportFragmentManager?.findFragmentByTag(WebViewFragment::class.java.simpleName)
					}"
		)

		return this is FragmentContainerActivity && this.supportFragmentManager.findFragmentByTag(WebViewFragment::class.java.simpleName) != null
	}

	/**
	 * 判断 WebView  是否在 Navigation  中打开的
	 */
	private fun Activity.isWebViewNavigationActivity(): Boolean {
		logJs(
			"isWebViewNavigationActivity", "当前的 Activity ==${this}, " +
					"当前 navigation 的  Fragment ==${(this as? FragmentActivity)?.getCurrentNavigationFragment()}"
		)

		// return this is MainActivity && this.getCurrentNavigationFragment() is WebViewFragment
		return true
	}


	override fun navigateTo(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			gotoWebByContainerActivity(data)
			function.onSuccess()
		}
	}

	/**
	 * 该方法使用 FragmentContainerActivity 进行实现，否则页面回退时整个 h5 页面会刷新
	 */
	private fun gotoWebByContainerActivity(h5NavigateToData: String?) {
		val h5NavigateToParam = GsonUtil.fromJson<H5NavigateToParam>(h5NavigateToData) ?: return

		val args = WebViewFragmentArgs.Builder(h5NavigateToParam.url).apply {
			setTitle(h5NavigateToParam.title)
			setIsNeedTitle(h5NavigateToParam.isNeedTitle())
		}.build().toBundle()

		webViewFragment.startFragment(WebViewFragment::class.java, args)
	}

	override fun setH5Data(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val h5SaveDataEntity = GsonUtil.fromJson<H5SaveDataEntity>(data)
			h5SaveDataEntity?.key?.also {
				H5DataHelper.putData(it, h5SaveDataEntity.value)
				function.onSuccess("保存数据成功")
			} ?: function.onFail("key 不可为空")
		}
	}

	override fun getH5Data(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val h5DataEntity = GsonUtil.fromJson<H5GetDataEntity>(data)
			if (h5DataEntity == null) {
				function.onFail("h5 传输的数据异常")
				return@commonRegisterHandler
			}

			h5DataEntity.key?.also {
				function.onSuccess(H5GetDataReturn(H5DataHelper.getData(it)))
			} ?: function.onFail("key 不可为空")
		}
	}

	override fun clearH5Data(handlerName: String) {
		commonRegisterHandler(handlerName) { _, function ->
			H5DataHelper.clearData()
			function.onSuccess()
		}
	}

	override fun redirectTo(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			MainScope().launch {

				//打开新的页面
				gotoWebByContainerActivity(data)

				// 延迟关闭页面，防止底部页面会有切换显示
				delay(300)

				// 打开新的 Web 页面后，关闭当前页面
				if (attachActivity.isWebViewContainerActivity()) {
					attachActivity.finish()
				} else if (attachActivity.isWebViewNavigationActivity()) {
					bridgeWebView.findNavController().popBackStack()
				}

				function.onSuccess()
			}
		}
	}

	override fun reLaunch(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			MainScope().launch {
				//打开新的页面
				gotoWebByContainerActivity(data)

				// 延迟关闭页面，防止底部页面会有切换显示, 测试发现延迟 300 毫秒效果较好
				delay(300)

				logJs(handlerName, "当前页面堆栈 -----------> ${ActivityUtils.getActivityList()}")

				ActivityUtils.getActivityList().forEach {
					logJs(handlerName, "当前正在处理的 Activity ---------->  $it")
					when {
						it == ActivityUtils.getTopActivity() -> {
							// 栈顶为新打开的 web 页面不关闭

							logJs(handlerName, "$it ----------> 栈顶页面不作处理")
						}
						it.isWebViewContainerActivity() -> {
							it.finish()

							logJs(handlerName, "$it ----------> isWebViewContainerActivity(关闭页面)")
						}
						it.isWebViewNavigationActivity() -> {
							val fragmentActivity = it as FragmentActivity
							val popSuccess =
								fragmentActivity.getCurrentNavigationFragment()?.findNavController()?.popBackStack()

							// val currentNavigationFragment =
							//     fragmentActivity.getCurrentNavigationFragment() ?: return@forEach
							//
							//
							// val popSuccess = fragmentActivity.supportFragmentManager.primaryNavigationFragment?.childFragmentManager
							//     ?.beginTransaction()
							//     ?.remove(currentNavigationFragment)
							//     ?.commitNowAllowingStateLoss()


							logJs(handlerName, "$it ----------> isWebViewNavigationActivity(回退页面--成功?: ${popSuccess})")
						}
					}
				}

				function.onSuccess()
			}
		}
	}

	override fun setStatusBarLightMode(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val lightModeParam = GsonUtil.fromJson<StatusBarLightModeParam>(data) ?: return@commonRegisterHandler
			webViewFragment.lifecycleScope.launchWhenStarted {
				webViewFragment.immersionBar {
					statusBarDarkFont(lightModeParam.isLightMode())
					if (lightModeParam.isLightMode()) {
						statusBarColorInt(Color.WHITE)
					} else {
						transparentStatusBar()
					}
				}

				function.onSuccess()
			}
		}
	}

	override fun setStatusBarColor(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			GsonUtil.fromJson<StatusBarColorParam>(data)?.color?.also {
				webViewFragment.lifecycleScope.launchWhenStarted {
					webViewFragment.immersionBar {
						statusBarColor(it)
					}
					function.onSuccess()
				}
			} ?: function.onFail("颜色值为 null")
		}
	}

	override fun getNetworkConnectType(handlerName: String) {
		commonRegisterHandler(handlerName) { _, function ->
			val connectType = when (NetworkUtils.getNetworkType()) {
				NetworkUtils.NetworkType.NETWORK_2G, NetworkUtils.NetworkType.NETWORK_3G,
				NetworkUtils.NetworkType.NETWORK_4G, NetworkUtils.NetworkType.NETWORK_5G -> "MOBILE"
				NetworkUtils.NetworkType.NETWORK_WIFI -> "WIFI"
				NetworkUtils.NetworkType.NETWORK_ETHERNET, NetworkUtils.NetworkType.NETWORK_UNKNOWN -> "UNKNOWN"
				else -> "NO"
			}
			function.onSuccess(GetNetworkConnectTypeReturn(connectType))
		}
	}

	override fun setWebView(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val h5SetWebViewParam = GsonUtil.fromJson<H5SetWebViewParam>(data) ?: return@commonRegisterHandler
			try {
				bridgeWebView.setBackgroundColor(Color.parseColor(h5SetWebViewParam.backgroundColor))
				function.onCallBack(H5Return.success())
			} catch (e: Exception) {
				function.onCallBack(H5Return.fail(e.message))
			}
		}
	}

	override fun getLocation(handlerName: String) {
		commonRegisterHandler(handlerName) { _, _ ->

		}
	}

	override fun previewImage(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val previewImageParam = GsonUtil.fromJson<PreviewImageParam>(data) ?: return@commonRegisterHandler

			XPopup.Builder(webViewFragment.requireContext())
				.isViewMode(true)
				.asImageViewer(
					null, previewImageParam.index, previewImageParam.urls, { popupView, position ->
						// popupView.updateSrcView()
					}, SmartGlideImageLoader()
				)
				.show()

			function.onSuccess()
		}
	}

	override fun savePhotoToAlbum(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val savePhotoToAlbumParam = GsonUtil.fromJson<SavePhotoToAlbumParam>(data) ?: return@commonRegisterHandler
			webViewFragment.reqPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, deniedAction = {
				function.onFail("获取存储权限失败")
			}) {
				webViewFragment.lifecycleScope.launch {
					val bitmap = BitmapUtil.getBitmapFromUrl(savePhotoToAlbumParam.fileUrl ?: "")
					bitmap?.run {
						val saveName = savePhotoToAlbumParam.fileName ?: "${getFormattedNowDateTime()}.png"

						BitmapUtil.saveBitmap(attachActivity, this, saveName, failAction = {
							function.onFail("保存图片失败")
						}) {
							function.onSuccess("保存图片成功")
						}
					} ?: function.onFail("获取图片失败")
				}
			}
		}
	}

	override fun callPhone(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val h5Call = GsonUtil.fromJson<H5CallParam>(data) ?: return@commonRegisterHandler
			val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + h5Call.phone))
			intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
			webViewFragment.startActivity(intent)
			function.onSuccess()
		}
	}

	override fun downloadFile(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val downLoadFileParam = GsonUtil.fromJson<DownLoadFileParam>(data) ?: return@commonRegisterHandler
			if (!downLoadFileParam.fileUrl.startsWith("http")) {
				function.onFail("文件下载地址有误！")
				return@commonRegisterHandler
			}

			DownloadFileUtil.startDownLoad(
				webViewFragment,
				downLoadFileParam.fileUrl,
				downLoadFileParam.fileName,
				listener = object : OnDownloadListener {
					override fun onStart(file: File?) {}

					override fun onProgress(file: File?, progress: Int) {}

					override fun onComplete(file: File?) {
						ScanFileActionUtil.scanMedia(webViewFragment.requireContext(), file?.absolutePath ?: "")
						function.onSuccess(file?.absolutePath)
					}

					override fun onError(file: File?, e: Exception?) {
						function.onFail(e?.message)
					}

					override fun onEnd(file: File?) {
					}
				})
		}
	}

	override fun previewFile(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val previewFileParam = GsonUtil.fromJson<PreviewFileParam>(data) ?: return@commonRegisterHandler
			val filename = previewFileParam.fileName
			val fileUrl = previewFileParam.fileUrl
			if (filename == null || fileUrl == null) {
				function.onFail("预览文件名或文件链接地址不可为空！")
			} else {
				PreviewFileActivity.start(webViewFragment.requireContext(), filename, fileUrl)

				function.onSuccess()
			}
		}
	}

	override fun scanQRCode(handlerName: String) {
		commonRegisterHandler(handlerName) { _, function ->
			val webViewFragment = webViewFragment as WebViewFragment

			webViewFragment.launchActivity(
				CaptureActivity::class.java,
				callback = object : OnActivityResult {
					override fun onResultOk(data: Intent?) {
						val scanQRCodeReturn = ScanQRCodeReturn(CameraScan.parseScanResult(data))
						function.onSuccess(scanQRCodeReturn)
					}

					override fun onResultCanceled(data: Intent?) {
						function.onFail("取消扫码")
					}
				})
		}
	}

	override fun share2Platform(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val share2PlatformParam = GsonUtil.fromJson<Share2PlatformParam>(data) ?: return@commonRegisterHandler
			val platformParam = share2PlatformParam.convert2SharePlatformParam()

			val shareListener = object : MyUMShareListener() {

				override fun onResult(platform: SHARE_MEDIA) {
					function.onSuccess("分享成功")
				}

				override fun onError(platform: SHARE_MEDIA, t: Throwable) {
					function.onFail("分享失败")
				}

				override fun onCancel(platform: SHARE_MEDIA) {
					function.onFail("分享取消")
				}
			}

			when (platformParam.platform) {
				SHARE_MEDIA.MORE -> {
					if (!doShareCustom(share2PlatformParam, function)) {
						// 友盟分享更多
						UMShareUtil.shareUMWebWithPlatform(attachActivity, platformParam, shareListener)
					}
				}
				else -> UMShareUtil.shareUMWebWithPlatform(attachActivity, platformParam, shareListener)
			}
		}
	}

	/**
	 * 自定义分享处理
	 */
	private fun doShareCustom(share2PlatformParam: Share2PlatformParam, function: CallBackFunction): Boolean {
		return when (share2PlatformParam.type) {
			"sms" -> {
				share2PlatformParam.smsContent?.run {
					SmsHelper(attachActivity).sendMessage(this, share2PlatformParam.phoneNumber) {
						if (it) {
							function.onSuccess("短信发送成功")
						} else {
							function.onFail("短信发送失败")
						}
					}
				}

				true
			}

			"copy" -> {
				ClipboardHelper(attachActivity).copyText(share2PlatformParam.link)
				function.onSuccess("复制链接成功")

				true
			}

			else -> {
				// 其他更多类型发送广播通知 APP 实现
				attachActivity.sendBroadcast(Intent(H5Constants.ACTION_SHARE_TO_MORE).apply {
					this.putExtra(H5Constants.ACTION_SHARE_MORE_DATA, share2PlatformParam)
				})

				// 存放更多分享对应的 CallbackFunction
				CallBackFunctionDataStore.putCallBackFunction(H5Constants.ACTION_SHARE_TO_MORE, function)

				true
			}
		}
	}
}