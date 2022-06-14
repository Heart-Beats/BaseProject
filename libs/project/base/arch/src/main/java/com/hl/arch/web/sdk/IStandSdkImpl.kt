package com.hl.arch.web.sdk

import android.app.Activity
import android.graphics.Color
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.NetworkUtils
import com.github.lzyzsd.jsbridge.BridgeHandler
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.google.gson.Gson
import com.gyf.immersionbar.ktx.immersionBar
import com.hl.arch.mvvm.activity.FragmentContainerActivity
import com.hl.arch.mvvm.activity.startFragment
import com.hl.arch.web.WebViewFragment
import com.hl.arch.web.WebViewFragmentArgs
import com.hl.arch.web.bean.*
import com.hl.arch.web.helpers.H5DataHelper
import com.hl.arch.web.helpers.logJs
import com.hl.uikit.getStatusBarHeight
import com.hl.umeng.sdk.MyUMShareListener
import com.hl.umeng.sdk.UMShareUtil
import com.hl.utils.ProxyHandler
import com.hl.utils.getCurrentNavigationFragment
import com.hl.utils.traverseFindFirstViewByType
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author  张磊  on  2022/06/13 at 15:14
 * Email: 913305160@qq.com
 */

class IStandSdkImpl(
	private val currentFragment: Fragment,
	private val bridgeWebView: BridgeWebView,
	private var bridgeHandlerProxy: ProxyHandler<BridgeHandler>

) : IStandSdk {

	/**
	 * 当前 WebViewFragment 依附的 Activity
	 */
	private val attachActivity = currentFragment.requireActivity()

	init {
		currentFragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
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
				super.onDestroy(owner)
				currentFragment.lifecycle.removeObserver(this)
			}
		})
	}

	private fun commonRegisterHandler(handlerName: String, bridgeHandler: BridgeHandler) {
		bridgeWebView.registerHandler(handlerName, bridgeHandlerProxy.bind(bridgeHandler))
	}

	override fun getDeviceInfo(handlerName: String) {
		commonRegisterHandler(handlerName) { _, function ->

			// h5 获取的状态栏高度需要除以 density
			val statusBarHeightDp =
				bridgeWebView.getStatusBarHeight() / bridgeWebView.resources.displayMetrics.density

			val isWifi = if (NetworkUtils.isWifiConnected()) 1 else 0
			val deviceInfo =
				H5DeviceInfo(AndroidDeviceInfo(statusBarHeight = statusBarHeightDp.toInt(), isWifi = isWifi))
			function.onCallBack(H5Return.success(deviceInfo))
		}
	}

	override fun navigateBack(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			bridgeWebView.h5NavigateBack(data, function)
		}
	}

	private fun BridgeWebView.h5NavigateBack(data: String?, function: CallBackFunction) {
		val h5NavigateBackParam = Gson().fromJson(data, H5NavigateBackParam::class.java)
		repeat(h5NavigateBackParam.step) {

			logJs("h5NavigateBack", "开始")

			// 获取栈顶的 Activity， 即当前的 Activity
			val currentActivity = ActivityUtils.getTopActivity()
			if (currentActivity.isWebViewContainerActivity()) {
				currentActivity.finish()
			} else if (currentActivity.isWebViewNavigationActivity()) {
				logJs("h5NavigateBack", "开始回退")
				findNavController().popBackStack()
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
				function.onCallBack(H5Return.success("页面刷新成功"))
			} ?: function.onCallBack(H5Return.fail("当前页面未找到 webView "))
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
			function.onCallBack(H5Return.success())
		}
	}

	/**
	 * 该方法使用 FragmentContainerActivity 进行实现，否则页面回退时整个 h5 页面会刷新
	 */
	private fun gotoWebByContainerActivity(h5NavigateToData: String?) {
		val h5NavigateToParam = Gson().fromJson(h5NavigateToData, H5NavigateToParam::class.java)

		val args = WebViewFragmentArgs.Builder(h5NavigateToParam.url).apply {
			setTitle(h5NavigateToParam.title)
			setIsNeedTitle(h5NavigateToParam.isNeedTitle())
		}.build().toBundle()

		currentFragment.startFragment(WebViewFragment::class.java, args)
	}

	override fun saveH5Data(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val h5SaveDataEntity = Gson().fromJson(data, H5SaveDataEntity::class.java)
			h5SaveDataEntity.key?.also {
				H5DataHelper.putData(it, h5SaveDataEntity.value)
				function.onCallBack(H5Return.success("保存数据成功"))
			} ?: function.onCallBack(H5Return.fail("key 不可为空"))
		}
	}

	override fun getH5Data(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val h5DataEntity = Gson().fromJson(data, H5GetDataEntity::class.java)
			if (h5DataEntity == null) {
				function.onCallBack(H5Return.fail("h5 传输的数据异常"))
				return@commonRegisterHandler
			}

			h5DataEntity.key?.also {
				function.onCallBack(H5Return.success(H5DataHelper.getData(it)))
			} ?: function.onCallBack(H5Return.fail("key 不可为空"))
		}
	}

	override fun clearH5Data(handlerName: String) {
		commonRegisterHandler(handlerName) { _, function ->
			H5DataHelper.clearData()
			function.onCallBack(H5Return.success())
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

				function.onCallBack(H5Return.success())
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

				function.onCallBack(H5Return.success())
			}
		}
	}

	override fun setStatusBarLightMode(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			val lightModeParam = Gson().fromJson(data, StatusBarLightModeParam::class.java)
			currentFragment.lifecycleScope.launchWhenStarted {
				currentFragment.immersionBar {
					statusBarDarkFont(lightModeParam.isLightMode())
					if (lightModeParam.isLightMode()) {
						statusBarColorInt(Color.WHITE)
					} else {
						transparentStatusBar()
					}
				}

				function.onCallBack(H5Return.success())
			}
		}
	}

	override fun setStatusBarColor(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			Gson().fromJson(data, StatusBarColorParam::class.java)?.color?.also {
				currentFragment.lifecycleScope.launchWhenStarted {
					currentFragment.immersionBar {
						statusBarColor(it)
					}
					function.onCallBack(H5Return.success())
				}
			} ?: function.onCallBack(H5Return.fail("颜色值为 null"))
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
			function.onCallBack(H5Return.success(GetNetworkConnectTypeReturn(connectType)))
		}
	}

	override fun getLocation(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->

		}
	}

	override fun previewImage(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->

		}
	}

	override fun savePhotoToAlbum(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->

		}
	}

	override fun callPhone(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->

		}
	}

	override fun downLoadFile(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->

		}
	}

	override fun scanQRCode(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->

		}
	}

	override fun share2Platform(handlerName: String) {
		commonRegisterHandler(handlerName) { data, function ->
			bridgeWebView.registerHandler(handlerName, bridgeHandlerProxy.bind { data, function ->
				val shareListener = object : MyUMShareListener() {

					override fun onResult(platform: SHARE_MEDIA) {
						function.onCallBack(H5Return.success("分享成功"))
					}

					override fun onError(platform: SHARE_MEDIA, t: Throwable) {
						function.onCallBack(H5Return.fail("分享失败"))
					}

					override fun onCancel(platform: SHARE_MEDIA) {
						function.onCallBack(H5Return.fail("分享取消"))
					}

				}

				val h5Share2PPlatformParam = Gson().fromJson(data, Share2PlatformParam::class.java)
				when (h5Share2PPlatformParam.type) {
					"" -> {
						UMShareUtil.shareUMWebWithPlatform(
							attachActivity,
							h5Share2PPlatformParam.convert2SharePlatformParam(),
							shareListener
						)
					}
				}
			})
		}
	}
}