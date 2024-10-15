package com.hl.web.pool

import android.content.Context
import android.content.MutableContextWrapper
import android.os.Looper
import android.view.ViewGroup
import com.hl.web.widgets.ProgressBridgeWebView
import com.hl.xloginit.xlogE

/**
 * @author  张磊  on  2024/10/14 at 10:57
 * Email: 913305160@qq.com
 *
 * 采用池化缓存 WebView，避免多次实例化带来的性能消耗
 */
internal object WebViewPoolManager {
	private const val TAG = "WebViewPoolManager"

	private val webViewCache: MutableList<ProgressBridgeWebView> = ArrayList(1)

	private fun create(context: Context): ProgressBridgeWebView {
		return ProgressBridgeWebView(context)
	}

	/**
	 * 初始化
	 */
	@JvmStatic
	fun prepare(context: Context) {
		if (webViewCache.isEmpty()) {
			Looper.getMainLooper()
			Looper.myQueue().addIdleHandler {
				webViewCache.add(create(MutableContextWrapper(context)))
				false
			}
		}
	}

	/**
	 * 获取 WebView
	 */
	@JvmStatic
	fun obtain(context: Context): ProgressBridgeWebView {
		if (webViewCache.isEmpty()) {
			webViewCache.add(create(MutableContextWrapper(context)))
		}
		val webView = webViewCache.removeAt(0)
		val contextWrapper = webView.context as MutableContextWrapper
		contextWrapper.baseContext = context
		webView.clearHistory()
		webView.resumeTimers()
		return webView
	}

	/**
	 * 回收 WebView
	 */
	@JvmStatic
	fun recycle(webView: ProgressBridgeWebView) {
		try {
			// 有音频播放的 web 页面的销毁逻辑： 在关闭了Activity时，如果 Webview 的音乐或视频，还在播放。就必须销毁 Webview
			// 但是注意：webview 调用 destory 时,webview 仍绑定在 Activity 上，这是由于构建 webview 时传入了该 Activity 的 context 对象
			// 因此需要先从父容器中移除webview,然后再销毁 webview:
			webView.stopLoading()
			webView.loadUrl("about:blank")
			webView.clearHistory()
			webView.pauseTimers()
			webView.clearFormData()
			webView.removeJavascriptInterface("webkit")

			// 重置并回收当前的上下文对象，根据池容量判断是否销毁，也可以置换为ApplicationContext
			val contextWrapper = webView.context as MutableContextWrapper
			contextWrapper.baseContext = webView.context.applicationContext

			val parent = webView.parent
			if (parent != null) {
				(parent as ViewGroup).removeView(webView)
			}
		} catch (e: Exception) {
			xlogE(TAG, "recycle 异常", tr = e)
		} finally {
			if (!webViewCache.contains(webView)) {
				webViewCache.add(webView)
			}
		}
	}

	/**
	 * 销毁 WebView
	 */
	@JvmStatic
	fun destroy() {
		try {
			webViewCache.forEach {
				it.removeAllViews()
				it.destroy()  // 在调用 webView.destroy() 方法后，WebView 实例已经被销毁，不能再用于加载页面或进行其他操作，因此避免过早调用 destroy()
				webViewCache.remove(it)
			}
		} catch (e: Exception) {
			xlogE(TAG, "destroy 异常", tr = e)
		}
	}

}