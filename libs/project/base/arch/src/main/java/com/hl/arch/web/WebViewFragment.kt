package com.hl.arch.web

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.TextPaint
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.AppUtils
import com.elvishew.xlog.XLog
import com.gyf.immersionbar.ktx.hideStatusBar
import com.gyf.immersionbar.ktx.immersionBar
import com.gyf.immersionbar.ktx.showStatusBar
import com.hl.arch.databinding.FragmentWebViewBinding
import com.hl.arch.mvvm.activity.startFragment
import com.hl.arch.mvvm.fragment.ViewBindingMvvmBaseFragment
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.arch.mvvm.vm.LiveDataVM
import com.hl.arch.web.client.MyBridgeWebViewClient
import com.hl.arch.web.client.MyWebChromeClient
import com.hl.arch.web.helpers.JsBridgeHelper
import com.hl.arch.web.helpers.logJs
import com.hl.uikit.ProgressWebView
import com.hl.uikit.gone
import com.hl.uikit.visible
import com.hl.utils.dp
import com.hl.utils.dpInt
import com.hl.utils.initInsetPadding


open class WebViewFragment : ViewBindingMvvmBaseFragment<FragmentWebViewBinding>() {

	companion object {

		/**
		 * 使用新的 FragmentContainerActivity 来打开 WebViewFragment
		 *     此种方式会让页面开启新的堆栈，防止回退时刷新页面
		 */
		fun startNewPage(
			currentFragment: Fragment, url: String,
			title: String? = null, isNeedTitle: Boolean = false
		) {
			startNewPage(currentFragment.requireActivity(), url, title, isNeedTitle)
		}

		fun startNewPage(
			currentActivity: FragmentActivity, url: String,
			title: String? = null, isNeedTitle: Boolean = false
		) {
			startNewPage(currentActivity as Context, url, title, isNeedTitle)
		}

		fun startNewPage(
			context: Context, url: String,
			title: String? = null, isNeedTitle: Boolean = false
		) {
			val args = WebViewFragmentArgs.Builder(url)
				.setTitle(title)
				.setIsNeedTitle(isNeedTitle)
				.build()
				.toBundle()
			context.startFragment(WebViewFragment::class.java, args)
		}
	}

	private lateinit var webView: ProgressWebView

	private lateinit var webChromeClient: MyWebChromeClient

	override fun FragmentWebViewBinding.onViewCreated(savedInstanceState: Bundle?) {
		val args = WebViewFragmentArgs.fromBundle(requireArguments())

		args.isNeedTitle.run {
			if (this) {
				initToolBar(args.title ?: "")
			} else {
				initStatusBar()
			}
		}

		initWebView(args)

		args.url.run {
			val uri = this.toUri()
			if (uri.scheme == "native") {
				onNativeRequestLoading(webView, uri)
			} else {
				JsBridgeHelper.registerHandlers(this@WebViewFragment, webView)
				webView.loadUrl(args.url)
			}
		}
	}

	private fun FragmentWebViewBinding.initToolBar(title: String) {
		toolbarLayout.visibility = View.VISIBLE
		toolbar?.run {
			this.title = title
		}
	}

	private fun initStatusBar() {
		// 该 Fragment 为整个单独页面时，修改状态栏
		if (!isMainPage()) {
			return
		}

		initInsetPadding(top = false)
		// 无标题栏时，状态栏透明，字体浅色
		immersionBar {
			statusBarDarkFont(false)
			transparentStatusBar()
		}
	}

	private fun FragmentWebViewBinding.initWebView(args: WebViewFragmentArgs) {
		webView = progressWebView.apply {
			setProgressDisplay(onLoadNeedProgress())
			settings.initWebSetting()
		}

		webChromeClient = getWebChromeClient(args)
		webView.webChromeClient = webChromeClient
		webView.webViewClient = getWebViewClient()
	}

	private fun initTitle(args: WebViewFragmentArgs, title: String?) {
		val paint = TextPaint()
		paint.textSize = 16f.dp

		val realTitle: String? = if (args.title.isNullOrEmpty()) {
			title
		} else {
			args.title
		}
		if (realTitle.isNullOrEmpty()) return

		val bounds = Rect()
		paint.getTextBounds(realTitle, 0, realTitle.length, bounds)
		val maxWidth = 200.dpInt
		if (bounds.width() > maxWidth) {
			val start = 0
			val end = (realTitle.length * (1.0f * maxWidth / bounds.width())).toInt()
			toolbar?.title = realTitle.substring(start, end) + "..."
		} else {
			toolbar?.title = realTitle
		}
	}

	private fun webViewShouldOverrideUrlLoading(view: WebView?, uri: Uri?): Boolean {
		XLog.i("url == $uri")
		if (uri?.scheme == "native") {
			XLog.i("url == $uri , 识别到自定义 native 链接")
			return onNativeRequestLoading(view, uri)
		}
		return false
	}

	override fun onLiveDataVMCreated(liveDataVM: LiveDataVM) {
	}

	override fun onFlowVMCreated(flowVM: FlowVM) {
	}

	/**
	 * 重写此方法可实现加载页面时是否需要进度显示, 默认不需要
	 */
	open fun onLoadNeedProgress(): Boolean {
		return false
	}

	/**
	 *  重写此方法实现 WebSetting 的定制化
	 */
	open fun WebSettings.initWebSetting() {
		this.javaScriptEnabled = true
		this.userAgentString = "${this.userAgentString}-${AppUtils.getAppName()}"
		this.domStorageEnabled = true
		this.defaultTextEncodingName = "utf-8"
		this.databaseEnabled = true
		this.useWideViewPort = true
		this.loadWithOverviewMode = true
		this.displayZoomControls = true
		this.cacheMode = WebSettings.LOAD_DEFAULT
	}

	/**
	 * 重写此方法实现 WebChromeClient 的自定义
	 */
	open fun FragmentWebViewBinding.getWebChromeClient(args: WebViewFragmentArgs) =
		object : MyWebChromeClient(this@WebViewFragment) {

			//用于全屏渲染视频的View
			private var mCustomView: View? = null

			private var mCustomViewCallback: CustomViewCallback? = null

			override fun onReceivedTitle(view: WebView?, title: String?) {
				super.onReceivedTitle(view, title)
				if (args.isNeedTitle) {
					initTitle(args, title)
				}
			}

			override fun onProgressChanged(view: WebView?, newProgress: Int) {
				webView.updateProgressBar(newProgress)
				super.onProgressChanged(view, newProgress)
			}

			override fun onShowCustomView(view: View?, callback: CustomViewCallback) {
				super.onShowCustomView(view, callback)

				//如果view 已经存在，则隐藏
				if (mCustomView != null) {
					callback.onCustomViewHidden()
					return
				}

				mCustomView = view
				mCustomView?.visible()
				mCustomViewCallback = callback
				videoLayout.addView(mCustomView)
				videoLayout.visible()
				videoLayout.bringToFront()

				//设置横屏
				requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
			}

			override fun onHideCustomView() {
				super.onHideCustomView()
				if (mCustomView == null) {
					return
				}
				mCustomView?.gone()
				videoLayout.removeView(mCustomView)
				videoLayout.gone()
				try {
					mCustomViewCallback!!.onCustomViewHidden()
				} catch (e: Exception) {
				}

				//竖屏
				requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
			}
		}


	/**
	 * 重写此方法实现 WebViewClient 的自定义
	 */
	open fun getWebViewClient() = object : MyBridgeWebViewClient(webView) {

		override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
			val uri = request?.url

			// BridgeWebViewClient 原有的 url 拦截处理不可去掉，否则可能页面跳转异常
			if (shouldOverrideUrlLoading(view, uri.toString())) {
				return true
			}

			if (webViewShouldOverrideUrlLoading(view, uri)) {
				return true
			}
			return super.shouldOverrideUrlLoading(view, request)
		}

		override fun onPageFinished(view: WebView?, url: String?) {
			super.onPageFinished(view, url)
			logJs("onH5Load", url)
			webView.callHandler("onH5Load", url) { }
		}
	}

	/**
	 * 重写此方法可实现自定义加载原生链接逻辑， 默认不拦截处理
	 */
	open fun onNativeRequestLoading(view: WebView?, uri: Uri): Boolean {
		return false
	}

	override fun onBackPressed() =
		if (this.webView.canGoBack()) {
			this.webView.goBack()
		} else {
			super.onBackPressed()
		}

	/**
	 * 载入显示 url 对应的网页
	 */
	fun loadUrl(url: String) {
		webView.loadUrl(url)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		webChromeClient.onActivityResult(requestCode, resultCode, data)
	}

	/**
	 * 横竖屏切换监听
	 */
	override fun onConfigurationChanged(config: Configuration) {
		super.onConfigurationChanged(config)
		val window = requireActivity().window
		when (config.orientation) {
			Configuration.ORIENTATION_LANDSCAPE -> {
				// window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
				// window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

				hideStatusBar()
			}
			Configuration.ORIENTATION_PORTRAIT -> {
				// window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
				// window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

				showStatusBar()
			}
		}
	}
}


/**
 * 非 Navigation 框架下使用
 * 打开新的 Web 页面， 会开启新的 Activity
 */
fun Fragment.navigateToWeb(url: String, title: String? = null, isNeedTitle: Boolean = false) {
	WebViewFragment.startNewPage(this, url, title, isNeedTitle)
}

/**
 * 非 Navigation 框架下使用
 * 打开新的 Web 页面， 会开启新的 Activity
 */
fun FragmentActivity.navigateToWeb(url: String, title: String? = null, isNeedTitle: Boolean = false) {
	WebViewFragment.startNewPage(this, url, title, isNeedTitle)
}

/**
 * 非 Navigation 框架下使用
 * 打开新的 Web 页面， 会开启新的 Activity
 */
fun Context.navigateToWeb(url: String, title: String? = null, isNeedTitle: Boolean = false) {
	WebViewFragment.startNewPage(this, url, title, isNeedTitle)
}