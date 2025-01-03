package com.hl.web

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.TextPaint
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.elvishew.xlog.XLog
import com.gyf.immersionbar.ktx.hideStatusBar
import com.gyf.immersionbar.ktx.immersionBar
import com.gyf.immersionbar.ktx.showStatusBar
import com.hl.activityresult.ActivityResultHelper
import com.hl.activityresult.OnActivityResult
import com.hl.ui.base.ViewBindingBaseFragment
import com.hl.ui.base.startFragment
import com.hl.ui.utils.dp
import com.hl.ui.utils.dpInt
import com.hl.ui.utils.gone
import com.hl.ui.utils.initInsetPadding
import com.hl.ui.utils.visible
import com.hl.web.client.MyBridgeWebViewClient
import com.hl.web.client.MyWebChromeClient
import com.hl.web.databinding.HlWebFragmentWebViewBinding
import com.hl.web.helpers.JsBridgeHelper
import com.hl.web.helpers.logJs
import com.hl.web.pool.WebViewPoolManager
import com.hl.web.widgets.ProgressBridgeWebView


open class WebViewFragment : ViewBindingBaseFragment<HlWebFragmentWebViewBinding>() {

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

	private lateinit var webView: ProgressBridgeWebView

	private lateinit var activityResultHelper: ActivityResultHelper

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activityResultHelper = ActivityResultHelper(this)
	}

	override fun HlWebFragmentWebViewBinding.onViewCreated(savedInstanceState: Bundle?) {
		val arguments = buildArgs()?.build()?.toBundle() ?: requireArguments()
		val args = WebViewFragmentArgs.fromBundle(arguments)

		args.isNeedTitle.run {
			if (this) {
				initToolBar(args.title ?: "")
			} else {
				initStatusBar()
			}
		}

		// AndroidBug5497WorkaroundJava.assistActivity(requireActivity())

		initWebView(args)

		loadUrl(args.url)
	}

	private fun HlWebFragmentWebViewBinding.initToolBar(title: String) {
		toolbarLayout.visible()
		webviewTitle.text=title
		// toolbar?.run {
		// 	this.title = title
		// }
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

	private fun HlWebFragmentWebViewBinding.initWebView(args: WebViewFragmentArgs) {
		webView = WebViewPoolManager.obtain(requireContext()).apply {
			setProgressDisplay(onLoadNeedProgress())
			settings.initWebSetting()
		}
		val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
		webviewContainer.addView(webView, layoutParams)

		webView.webChromeClient = getWebChromeClient(args)
		webView.webViewClient = getWebViewClient()
	}

	private fun initTitle(args: WebViewFragmentArgs, title: String?) {
		if (this.view == null) {
			return
		}

		if (this.view != null) {
			// 回退页面时由于 WebViewPoolManager 中 loadUrl ("about:blank")，因此会收到回调
			if ("about:blank" == title) {
				// loadUrl("about:blank") 导致了存在页面历史栈，导致返回时，会加载该页面，回退时直接退出不加载标题
				return
			}
		}

		val paint = TextPaint()
		paint.textSize = 16F.dp

		val realTitle: String? = if (args.title.isNullOrEmpty()) {
			title
		} else {
			args.title
		}
		if (realTitle.isNullOrEmpty()) return

		val bounds = Rect()
		paint.getTextBounds(realTitle, 0, realTitle.length, bounds)
		val maxWidth = 200.dpInt

		val webviewTitle= viewBinding.webviewTitle
		if (bounds.width() > maxWidth) {
			val start = 0
			val end = (realTitle.length * (1.0f * maxWidth / bounds.width())).toInt()
			// toolbar?.title = realTitle.substring(start, end) + "..."
			webviewTitle.text=realTitle.substring(start, end) + "..."
		} else {
			// toolbar?.title = realTitle
			webviewTitle.text=realTitle
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

	/**
	 * 重写此方法可构建初始化参数，设置载入的 url、标题、以及是否需要标题
	 */
	protected open fun buildArgs(): WebViewFragmentArgs.Builder? = null

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

		val userAgent = JsBridgeHelper.getUserAgent()
		val appUserAgent = if (userAgent.isNullOrBlank()) "" else "-${userAgent}"
		this.userAgentString = "${this.userAgentString}${appUserAgent}"
		this.domStorageEnabled = true
		this.defaultTextEncodingName = "utf-8"
		this.databaseEnabled = true
		this.useWideViewPort = true
		this.loadWithOverviewMode = true
		this.displayZoomControls = true
		this.cacheMode = WebSettings.LOAD_DEFAULT

		// 设置是否开启密码保存功能，不建议开启，默认已经做了处理，存在盗取密码的危险
		this.savePassword = false
	}

	/**
	 * 重写此方法实现 WebChromeClient 的自定义
	 */
	open fun HlWebFragmentWebViewBinding.getWebChromeClient(args: WebViewFragmentArgs) =
		object : MyWebChromeClient(this@WebViewFragment) {

			// 用于全屏渲染视频的View
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

				// 如果view 已经存在，则隐藏
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

				// 设置横屏
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

				// 竖屏
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

		override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
			if ("about:blank" == url) {
				// 回退页面时由于 WebViewPoolManager 中 loadUrl ("about:blank")，因此会收到回调
				if (this@WebViewFragment.view != null) {
					// loadUrl("about:blank") 导致了存在页面历史栈，导致返回时，会加载该页面，回退时直接退出
					superBackPressed()
				}
				return
			}
			super.onPageStarted(view, url, favicon)
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
			superBackPressed()
		}

	private fun superBackPressed() {
		super.onBackPressed()
	}


	override fun onResume() {
		super.onResume()
		webView.settings.javaScriptEnabled = true
	}

	override fun onStop() {
		super.onStop()

		// 注意不能关闭 JS 交互，否则标签选择图片或文件非系统选择器会有问题
		// WebView在后台的时候, 此时关闭js交互， 避免后台无法释放 js 导致发热耗电
		// webView.settings.javaScriptEnabled = false
	}

	override fun onDestroy() {
		WebViewPoolManager.recycle(webView)
		super.onDestroy()
	}


	/**
	 * 重新载入页面
	 */
	fun reload() {
		webView.reload()
	}

	/**
	 * 载入显示 url 对应的网页
	 */
	fun loadUrl(url: String) {
		val uri = url.toUri()
		if (uri.scheme == "native") {
			onNativeRequestLoading(webView, uri)
		} else {
			JsBridgeHelper.registerHandlers(this@WebViewFragment, webView)
			webView.loadUrl(url)
		}
	}

	/**
	 * 启动 Activity 并返回处理结果
	 */
	fun launchActivity(
		launchActivityCls: Class<out Activity>,
		callback: OnActivityResult,
	) {
		activityResultHelper.launchActivity(launchActivityCls, callback = callback)
	}

	/**
	 * 启动 Intent 并返回处理结果
	 */
	fun launchIntent(
		launchIntent: Intent,
		callback: OnActivityResult,
	) {
		activityResultHelper.launchIntent(launchIntent, callback = callback)
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
fun Context.navigateToWeb(url: String, title: String? = null, isNeedTitle: Boolean = false) {
	WebViewFragment.startNewPage(this, url, title, isNeedTitle)
}

fun View.navigateToWeb(url: String, title: String? = null, isNeedTitle: Boolean = false) {
	WebViewFragment.startNewPage(this.context, url, title, isNeedTitle)
}