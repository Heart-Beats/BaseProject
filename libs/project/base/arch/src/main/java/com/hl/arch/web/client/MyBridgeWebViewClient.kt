package com.hl.arch.web.client

import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.elvishew.xlog.XLog
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

/**
 * @author  张磊  on  2021/12/21 at 14:41
 * Email: 913305160@qq.com
 *
 * BridgeWebView 设置的 webViewClient 必须继承自 BridgeWebViewClient
 */
open class MyBridgeWebViewClient(val webView: BridgeWebView) : BridgeWebViewClient(webView) {

	private var isPageLoadFail = false

	@Deprecated("Deprecated in Java")
	override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
		var decodeUrl = url
		try {
			decodeUrl = URLDecoder.decode(decodeUrl, "UTF-8")
		} catch (e: UnsupportedEncodingException) {
			XLog.d("shouldOverrideUrlLoading: ", e)
		}
		if (decodeUrl.startsWith("tel:")) {
			val intent = Intent(Intent.ACTION_VIEW, Uri.parse(decodeUrl))
			webView.context.startActivity(intent)
			return true
		}
		return super.shouldOverrideUrlLoading(view, url)
	}

	override fun onReceivedSslError(webView: WebView?, handler: SslErrorHandler, sslError: SslError?) {
		handler.proceed()
	}

	override fun onPageFinished(view: WebView?, url: String?) {
		super.onPageFinished(view, url)

		XLog.d("页面加载结束， url == $url")

		if (isPageLoadFail) {
			onPageLoadFail(url)
		} else {
			onPageLoadSuccess(url)
		}
	}

	protected open fun onPageLoadFail(url: String?) {
		XLog.d("页面加载失败， url == $url")
	}

	protected open fun onPageLoadSuccess(url: String?) {
		XLog.d("页面加载成功， url == $url")
	}

	/**
	 * 网页内请求相关资源失败回调
	 */
	override fun onReceivedHttpError(
		view: WebView?,
		request: WebResourceRequest?,
		errorResponse: WebResourceResponse?
	) {
		val failingUrl = request?.url.toString()
		val errorStatusCode = errorResponse?.statusCode ?: 0
		val reasonPhrase = errorResponse?.reasonPhrase

		if (request?.isForMainFrame == true) {
			XLog.d("网页请求失败：errorCode = $errorStatusCode, description = $reasonPhrase, failingUrl =$failingUrl")

			onReceivedError(view, errorStatusCode, reasonPhrase, failingUrl)
		}
	}

	/**
	 * 网页加载失败回调
	 */
	@Deprecated("Deprecated in Java")
	override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
		XLog.d("网页加载失败：errorCode = $errorCode, description = $description, failingUrl = $failingUrl")

		isPageLoadFail = true
	}
}